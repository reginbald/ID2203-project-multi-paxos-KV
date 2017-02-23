package se.kth.id2203.epfd;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class HeartbeatRequest implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -6581545153388189529L;
    private final int seq;

    public HeartbeatRequest(int seq) {
        this.seq = seq;
    }
}
