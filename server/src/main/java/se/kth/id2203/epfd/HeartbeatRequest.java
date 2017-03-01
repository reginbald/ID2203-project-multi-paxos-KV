package se.kth.id2203.epfd;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class HeartbeatRequest implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -6581545153388189529L;
    public final int seq;

    public HeartbeatRequest(int seq) {
        this.seq = seq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeartbeatRequest that = (HeartbeatRequest) o;

        return seq == that.seq;
    }

    @Override
    public int hashCode() {
        return seq;
    }
}
