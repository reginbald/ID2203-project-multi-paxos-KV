package se.kth.id2203.epfd;

import com.google.common.collect.Sets;
import com.oracle.tools.packager.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.network.PL_Deliver;
import se.kth.id2203.network.PL_Send;
import se.kth.id2203.network.Partition;
import se.kth.id2203.network.PerfectLink;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Address;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EPFD extends ComponentDefinition {
    private static Logger logger = LoggerFactory.getLogger(EPFD.class);
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Negative<Bootstrapping> boot2 = provides(Bootstrapping.class);

    // component fields
    public final Positive<Timer> timer = requires(Timer.class);
    public final Positive<PerfectLink> perfectLink = requires(PerfectLink.class);
    public final Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);
    // Todo: What is positive and what is negative ?
    private NetAddress self = config().getValue("id2203.project.address", NetAddress.class);// TODO:Does this work?
    private Set<NetAddress> topology = new HashSet<>();

    private long delta = 30000; //config().getValue("id2203.project.epfd.delta", Long.class); // TODO:Does this work?

    //mutable state
    private long period = 50000; //config().getValue("id2203.project.epfd.delay", Long.class); // TODO:Does this work?;
    private Set<NetAddress> alive = new HashSet<>();//Collections.emptySet();
    private Set<NetAddress> suspected = new HashSet<>();
    private int seqnum = 0;

    private void startTimer(long delay) {
        logger.info("startTimer called with delay: {}", delay);
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

    Handler<AllNodes> initHandler = new Handler<AllNodes>(){
        @Override
        public void handle(AllNodes all) {
            logger.info("Init: {}", all.nodes);
            topology = all.nodes;
        }
    };

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
                logger.info("increasing delta to : {}", period + delta);
                period = period + delta;
            }

            seqnum = seqnum + 1;

            logger.info("Suspected size {} ", suspected.size());
            for (NetAddress a : topology) {
                logger.info("Looping node {}", a.toString());
                if(!alive.contains(a) && !suspected.contains(a)) {
                    logger.info("Suspecting node {} adding it to suspected", a.toString());
                    suspected.add(a);
                    trigger(new Suspects(suspected), boot2); //send suspects to overlay manager
                    trigger(new Suspect(a), epfd);
                }
                else if (alive.contains(a) && suspected.contains(a)) {
                    logger.info("Removing node {} from suspected", a.toString());
                    suspected.remove(a);
                    trigger(new Suspects(suspected), boot2); // send suspects to overlay manager
                    trigger(new Restore(a), epfd);
                }
                trigger(new PL_Send(a, new HeartbeatRequest(seqnum)), perfectLink);
            }

            alive.clear();
            startTimer(period);
        }
    };

    protected final ClassMatchedHandler<HeartbeatRequest,PL_Deliver> hbRequestHandler = new ClassMatchedHandler<HeartbeatRequest, PL_Deliver>() {
        @Override
        public void handle(HeartbeatRequest heartbeatRequest, PL_Deliver message) {
            logger.info("received hbRequest from {} ", message.src);
            trigger(new PL_Send(self, new HeartbeatReply(seqnum)), perfectLink);
        }
    };

    protected final ClassMatchedHandler<HeartbeatReply, PL_Deliver> hbReplyHandler = new ClassMatchedHandler<HeartbeatReply, PL_Deliver>() {
        @Override
        public void handle(HeartbeatReply heartbeatReply, PL_Deliver message) {
            if(heartbeatReply.seq == seqnum || suspected.contains(message.src)) {
                alive.add(message.src);
            }
        }
    };
    {
        subscribe(startHandler, control);
        subscribe(timeoutHandler, timer);
        subscribe(hbRequestHandler, perfectLink);
        subscribe(hbReplyHandler, perfectLink);
        subscribe(initHandler, boot);
    }
}
