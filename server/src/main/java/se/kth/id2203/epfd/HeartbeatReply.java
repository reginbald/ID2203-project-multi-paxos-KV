package se.kth.id2203.epfd;

import se.sics.kompics.KompicsEvent;

public class HeartbeatReply implements KompicsEvent {
    public final int seq;

    public HeartbeatReply(int seq) {
        this.seq = seq;
    }
}
