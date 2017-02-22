package se.kth.id2203.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.atomicregister.ReadImposeWriteConsultMajorityComponent;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.bootstrapping.GetInitialAssignments;
import se.kth.id2203.bootstrapping.InitialAssignments;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;

import java.util.Set;

public class BasicBroadcast extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(BasicBroadcast.class);

    //subscriptions
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Positive<PerfectLink> pLink = requires(PerfectLink.class);

    protected final Negative<BestEffortBroadcast> beb = provides(BestEffortBroadcast.class);

    Set<NetAddress> topology;
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);

    Handler<Partition> initHandler = new Handler<Partition>(){
        @Override
        public void handle(Partition partition) {
            LOG.info("Init");
            topology = partition.nodes;
        }
    };

    Handler<BEB_Broadcast> bebBroadcastHandler = new Handler<BEB_Broadcast>() {
        @Override
        public void handle(BEB_Broadcast b) {
            LOG.info("bebBroadcastHandler");
            for (NetAddress t : topology) {
                trigger(new PL_Send(b.request_id, b.request_source, t, b.payload), pLink);
            }
        }
    };

    Handler<PL_Deliver> plDeliverHandler = new Handler<PL_Deliver>() {
        @Override
        public void handle(PL_Deliver p) {
            LOG.info("plDeliverHandler");
            trigger(new BEB_Deliver(p.request_id, p.request_source, p.src, p.payload), beb);
        }
    };

    {
        subscribe(initHandler, boot);
        subscribe(bebBroadcastHandler, beb);
        subscribe(plDeliverHandler, pLink);
    }
}