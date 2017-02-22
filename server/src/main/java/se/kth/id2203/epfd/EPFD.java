package se.kth.id2203.epfd;

import com.oracle.tools.packager.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    // final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    //val topology = cfg.getValue[List[Address]]("epfd.simulation.topology");
    //val delta = cfg.getValue[Long]("epfd.simulation.delay");

    //mutable state
    private long period;
    private Set<NetAddress> alive;
    private Set<NetAddress> suspected;
    private int seqnum = 0;

    /*var period = cfg.getValue[Long]("epfd.simulation.delay");
    var alive = Set(cfg.getValue[List[Address]]("epfd.simulation.topology"): _*);
    var suspected = Set[Address]();
    var seqnum = 0;

    def startTimer(delay: Long): Unit = {
        val scheduledTimeout = new ScheduleTimeout(period);
        scheduledTimeout.setTimeoutEvent(CheckTimeout(scheduledTimeout));
        trigger(scheduledTimeout -> timer);
    }*/

    //EPFD event handlers
    /*ctrl uponEvent {
        case _: Start => handle {

            startTimer(delta)
        }
    }*/
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

}
