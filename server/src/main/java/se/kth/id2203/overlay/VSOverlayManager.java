/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id2203.overlay;

import com.google.common.collect.Iterables;
import com.larskroll.common.J6;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.Booted;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.bootstrapping.GetInitialAssignments;
import se.kth.id2203.bootstrapping.InitialAssignments;
import se.kth.id2203.epfd.AllNodes;
import se.kth.id2203.epfd.EventuallyPerfectFailureDetector;
import se.kth.id2203.epfd.Suspects;
import se.kth.id2203.kvstore.OpResponse;
import se.kth.id2203.kvstore.Operation;
import se.kth.id2203.network.Partition;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

/**
 * The V(ery)S(imple)OverlayManager.
 * <p>
 * Keeps all nodes in a single partition in one replication group.
 * <p>
 * Note: This implementation does not fulfill the project task. You have to
 * support multiple partitions!
 * <p>
 * @author Lars Kroll <lkroll@kth.se>
 */
public class VSOverlayManager extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(VSOverlayManager.class);
    //******* Ports ******
    protected final Negative<Routing> route = provides(Routing.class);
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Negative<Bootstrapping> boot2 = provides(Bootstrapping.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    protected final Positive<EventuallyPerfectFailureDetector> epfd = requires(EventuallyPerfectFailureDetector.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    final int replication_degree = config().getValue("id2203.project.replication_degree", Integer.class);
    private LookupTable lut = null;
    private Set<NetAddress> suspects = new HashSet<>();
    //******* Handlers ******
    protected final Handler<GetInitialAssignments> initialAssignmentHandler = new Handler<GetInitialAssignments>() {

        @Override
        public void handle(GetInitialAssignments event) {
        LOG.info("Generating LookupTable...");
        LookupTable lut = LookupTable.generate(event.nodes, replication_degree);
        LOG.debug("Generated assignments:\n{}", lut);
        trigger(new InitialAssignments(lut), boot);
        }
    };
    protected final Handler<Booted> bootHandler = new Handler<Booted>() {

        @Override
        public void handle(Booted event) {
        if (event.assignment instanceof LookupTable) {
            LOG.info("Got NodeAssignment, overlay ready.");
            lut = (LookupTable) event.assignment;
            trigger(new AllNodes(lut.getAllNodes()), boot2); // Picked up by failure detector
            trigger(new Partition(lut.getPartition(self)), boot2); // Picked up by other components
        } else {
            LOG.error("Got invalid NodeAssignment type. Expected: LookupTable; Got: {}", event.assignment.getClass());
        }
        }
    };

    protected final Handler<Suspects> suspectsHandler = new Handler<Suspects>() {
        @Override
        public void handle(Suspects event) {
            LOG.info("Suspects: {}", event.suspects);
            suspects = new HashSet<>(event.suspects);
            trigger(new Partition(lut.getPartitionWithOutSuspects(self, event.suspects)), boot2);
        }
    };

    protected final ClassMatchedHandler<RouteMsg, Message> routeHandler = new ClassMatchedHandler<RouteMsg, Message>() {

        @Override
        public void handle(RouteMsg content, Message context) {
            Collection<NetAddress> partition = lut.lookup(content.key, suspects);
            if (partition.size() <= 0){
                Operation op = (Operation) content.msg;
                trigger(new Message(self, context.getSource(), new OpResponse(op.id, OpResponse.Code.ERROR, "")), net);
            } else {
                NetAddress target = SelectLeader(partition);
                LOG.info("Forwarding message for key {} to {}", content.key, target);
                trigger(new Message(context.getSource(), target, content.msg), net);
            }
        }
    };
    protected final Handler<RouteMsg> localRouteHandler = new Handler<RouteMsg>() {

        @Override
        public void handle(RouteMsg event) {
        Collection<NetAddress> partition = lut.lookup(event.key, suspects);
        NetAddress target = SelectLeader(partition);
        LOG.info("Routing message for key {} to {}", event.key, target);
        trigger(new Message(self, target, event.msg), net);
        }
    };
    protected final ClassMatchedHandler<Connect, Message> connectHandler = new ClassMatchedHandler<Connect, Message>() {

        @Override
        public void handle(Connect content, Message context) {
        if (lut != null) {
            LOG.debug("Accepting connection request from {}", context.getSource());
            int size = lut.getNodes().size();
            trigger(new Message(self, context.getSource(), content.ack(size)), net);
        } else {
            LOG.info("Rejecting connection request from {}, as system is not ready, yet.", context.getSource());
        }
        }
    };

    private NetAddress SelectLeader(Collection<NetAddress> partition){

        NetAddress out = null;
        int rank = 0;
        for (NetAddress addr : partition) {
            if (rank < rank(addr)){
                rank = rank(addr);
                out = addr;
            }
        }
        return out;
    }

    private int rank(NetAddress adr){
        return Math.abs(adr.getIp().hashCode() + adr.getPort());
    }

    {
        subscribe(initialAssignmentHandler, boot);
        subscribe(suspectsHandler, epfd);
        subscribe(bootHandler, boot);
        subscribe(routeHandler, net);
        subscribe(localRouteHandler, route);
        subscribe(connectHandler, net);
    }
}
