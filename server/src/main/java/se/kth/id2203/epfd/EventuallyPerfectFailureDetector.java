package se.kth.id2203.epfd;

import se.sics.kompics.Port;
import se.sics.kompics.PortType;
import se.sics.kompics.Positive;
import se.sics.kompics.timer.Timer;

public class EventuallyPerfectFailureDetector extends PortType {
    {
        indication(Suspect.class);
        indication(Restore.class);
    }

}
