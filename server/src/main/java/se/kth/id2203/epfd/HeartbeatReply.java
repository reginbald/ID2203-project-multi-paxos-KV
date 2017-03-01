package se.kth.id2203.epfd;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class HeartbeatReply implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -5584545153388129579L;
    public final int seq;

    public HeartbeatReply(int seq) {
        this.seq = seq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeartbeatReply that = (HeartbeatReply) o;

        return seq == that.seq;
    }

    @Override
    public int hashCode() {
        return seq;
    }
}
