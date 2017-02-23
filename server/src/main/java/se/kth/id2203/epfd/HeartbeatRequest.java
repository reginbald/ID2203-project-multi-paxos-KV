package se.kth.id2203.epfd;

import se.sics.kompics.KompicsEvent;

public class HeartbeatRequest implements KompicsEvent{
    private final int seq;

    public HeartbeatRequest(int seq) {
        this.seq = seq;
    }
}
