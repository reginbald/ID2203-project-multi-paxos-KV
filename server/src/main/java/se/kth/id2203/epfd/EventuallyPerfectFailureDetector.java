package se.kth.id2203.epfd;

import se.sics.kompics.Port;
import se.sics.kompics.PortType;

public class EventuallyPerfectFailureDetector extends PortType {
    {
        indication(Suspect.class);
        indication(Restore.class);
    }
}
