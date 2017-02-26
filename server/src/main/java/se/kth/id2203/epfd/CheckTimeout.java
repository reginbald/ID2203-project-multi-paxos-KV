package se.kth.id2203.epfd;


import se.sics.kompics.timer.ScheduleTimeout;

// case class CheckTimeout(timeout: ScheduleTimeout) extends CheckTimeout(timeout);
public class CheckTimeout extends se.sics.kompics.timer.Timeout{
    protected CheckTimeout(ScheduleTimeout request) {
        super(request);
    }
}
