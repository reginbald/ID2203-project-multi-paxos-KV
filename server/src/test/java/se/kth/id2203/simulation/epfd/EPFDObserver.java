package se.kth.id2203.simulation.epfd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.network.Partition;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.simulation.beb.BEBObserver;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EPFDObserver extends ComponentDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(BEBObserver.class);

    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);

    private final int aliveNodes;
    private UUID timerId;

    public EPFDObserver(Init init) {
        aliveNodes = init.aliveNodes;
        subscribe(handleStart, control);
        subscribe(handleCheck, timer);
    }

    private void schedulePeriodicCheck() {
        long period = config().getValue("pingpong.simulation.checktimeout", Long.class);
        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(period, period);
        BEBObserver.CheckTimeout timeout = new BEBObserver.CheckTimeout(spt);
        spt.setTimeoutEvent(timeout);
        trigger(spt, timer);
        timerId = timeout.getTimeoutId();
    }

    public static class CheckTimeout extends Timeout {

        public CheckTimeout(SchedulePeriodicTimeout spt) {
            super(spt);
        }
    }

    Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            schedulePeriodicCheck();
        }
    };

    Handler<BEBObserver.CheckTimeout> handleCheck = new Handler<BEBObserver.CheckTimeout>() {
        @Override
        public void handle(BEBObserver.CheckTimeout event) {
            GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        }
    };

    public static class Init extends se.sics.kompics.Init<EPFDObserver> {

        public final int aliveNodes;

        public Init(int aliveNodes) {
            this.aliveNodes = aliveNodes;
        }
    }
}
