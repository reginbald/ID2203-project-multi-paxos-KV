package se.kth.id2203.epfd;

import se.sics.kompics.KompicsEvent;

public class HeartbeatReply implements KompicsEvent {
    private final int seq;

    public HeartbeatReply(int seq) {
        this.seq = seq;
    }
}
