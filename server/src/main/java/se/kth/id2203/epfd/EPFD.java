package se.kth.id2203.epfd;

import com.google.common.collect.Sets;
import com.oracle.tools.packager.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.network.PL_Send;
import se.kth.id2203.network.PerfectLink;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Address;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

import java.util.List;
import java.util.Set;

public class EPFD extends ComponentDefinition {
    private static Logger logger = LoggerFactory.getLogger(EPFD.class);
    // component fields
    public final Positive<Timer> timer = requires(Timer.class);
    public final Positive<PerfectLink> perfectLink = requires(PerfectLink.class);
    public final Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);
    // Todo: What is positive and what is negative ?
    private NetAddress self = config().getValue("id2203.project.address", NetAddress.class);// TODO:Does this work?
    private Set<NetAddress> topology;
    private long delta = config().getValue("id2203.project.epfd.delta", Long.class); // TODO:Does this work?

    //mutable state
    private long period = config().getValue("id2203.project.epfd.delay", Long.class); // TODO:Does this work?;
    private Set<NetAddress> alive;
    private Set<NetAddress> suspected;
    private int seqnum = 0;

    private void startTimer(long delay) {
        SchedulePeriodicTimeout timeout =  new SchedulePeriodicTimeout(delay,delay);
        Timeout t;
        t = new Timeout(timeout) {
            @Override
            public RequestPathElement getTopPathElement() {
                return super.getTopPathElement();
            }
        };

        timeout.setTimeoutEvent(t);
        trigger(timeout, timer);
    }

    protected final Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start start) {
            Log.info("EPFD startHandler running");
            startTimer(delta);
        }
    };

    protected final Handler<Timeout> timeoutHandler = new Handler<Timeout>() {
        @Override
        public void handle(Timeout timeout) {
            logger.info("EPFD timeoutHandler called");
            if(!(Sets.intersection(suspected,alive).size() == 0)) {
                period = period + delta;
            }

            seqnum = seqnum + 1;

            for (NetAddress a : topology) {
                if(!alive.contains(a) && ! suspected.contains(a)) {
                    logger.info("Suspecting node {} adding it to suspected", a.toString());
                    suspected.add(a);
                    trigger(new Suspect(a), epfd);
                }
                else if (alive.contains(a) && suspected.contains(a)) {
                    logger.info("Removing node {} from suspected", a.toString());
                    suspected.remove(a);
                    trigger(new Restore(a), epfd);
                }
                trigger(new PL_Send(a, new HeartbeatRequest(seqnum)), perfectLink);
            }

            alive.clear();
        }
    };


}
