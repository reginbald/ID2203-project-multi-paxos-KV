package se.kth.id2203.simulation.beb;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.atomicregister.READ;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.epfd.AllNodes;
import se.kth.id2203.epfd.CheckTimeout;
import se.kth.id2203.network.*;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.simulation.SimulationResultMap;
import se.kth.id2203.simulation.SimulationResultSingleton;
import se.kth.id2203.simulation.keyvalue.ScenarioClient;
import se.sics.kompics.*;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

public class ScenarioBEBClient extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(ScenarioClient.class);
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    protected final Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);

    protected final Negative<Bootstrapping> boot = provides(Bootstrapping.class);

    //******* Fields ******
    private final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private final NetAddress server = config().getValue("id2203.project.bootstrap-address", NetAddress.class);
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();
    private final Map<UUID, String> pending = new TreeMap<>();

    private int received = 0;
    private int sent = 0;
    //******* Handlers ******

    protected final Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);

        Set<NetAddress> topology = new HashSet<>();
        for (Address a : gv.getAliveNodes().values()) {
            topology.add(new NetAddress(a.getIp(), a.getPort()));
        }
        if(topology.size() == 5) {
            for (NetAddress addr : topology) {
                trigger(new Message(null, addr, new Partition(topology)), net);
            }
        }
        }
    };

    protected final ClassMatchedHandler<READ, BEB_Deliver> responseHandler = new ClassMatchedHandler<READ, BEB_Deliver>() {
        @Override
        public void handle(READ r, BEB_Deliver b) {
            LOG.debug("Got BEB_Deliver: {}", r);
            res.put(self.toString()+"got", ++received);
        }
    };

    protected final ClassMatchedHandler<Partition, Message> partitionHandler = new ClassMatchedHandler<Partition, Message>() {

        @Override
        public void handle(Partition content, Message context) {
            LOG.debug("Got partition: {}", content);
            trigger(content, boot);
            res.put(self.toString()+"sent", ++sent);
            trigger(new BEB_Broadcast(new READ(UUID.randomUUID(), self, "1", 1)), beb);
            startTimer(1000);
        }
    };

    protected final Handler<Timeout> timeoutHandler = new Handler<Timeout>() {
        @Override
        public void handle(Timeout timeout) {
            res.put(self.toString()+"sent", ++sent);
            trigger(new BEB_Broadcast(new READ(UUID.randomUUID(), self, "1", 1)), beb);
            startTimer(1000);
        }
    };

    private void startTimer(long delay) {
        LOG.info("startTimer called with delay: {}", delay);

        ScheduleTimeout scheduledTimeout = new ScheduleTimeout(delay);
        scheduledTimeout.setTimeoutEvent(new CheckTimeout(scheduledTimeout));
        trigger(scheduledTimeout, timer);
    }

    {
        subscribe(startHandler, control);
        subscribe(timeoutHandler, timer);
        subscribe(partitionHandler, net);
        subscribe(responseHandler, beb);
    }
}
