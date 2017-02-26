package se.kth.id2203.simulation.beb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.network.Partition;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class BEBObserver extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(BEBObserver.class);

    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);

    private final int aliveNodes;

    private UUID timerId;

    public BEBObserver(Init init) {
        aliveNodes = init.aliveNodes;

        subscribe(handleStart, control);
        subscribe(handleCheck, timer);
    }

    Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            schedulePeriodicCheck();
        }
    };

    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timerId), timer);
    }

    Handler<CheckTimeout> handleCheck = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout event) {
            GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);

            if(gv.getAliveNodes().size() >= aliveNodes) {
                LOG.info("ALIVE - " + aliveNodes);
                Set<NetAddress> partition = new HashSet<>();
                for (Address a : gv.getAliveNodes().values()) {
                    partition.add(new NetAddress(a.getIp(), a.getPort()));
                }
                for (NetAddress addr : partition) {
                    trigger(new Message(null, addr, new Partition(partition)), network);
                }
                gv.terminate();
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<BEBObserver> {

        public final int aliveNodes;

        public Init(int aliveNodes) {
            this.aliveNodes = aliveNodes;
        }
    }

    private void schedulePeriodicCheck() {
        long period = config().getValue("pingpong.simulation.checktimeout", Long.class);
        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(period, period);
        CheckTimeout timeout = new CheckTimeout(spt);
        spt.setTimeoutEvent(timeout);
        trigger(spt, timer);
        timerId = timeout.getTimeoutId();
    }

    public static class CheckTimeout extends Timeout {

        public CheckTimeout(SchedulePeriodicTimeout spt) {
            super(spt);
        }
    }
}