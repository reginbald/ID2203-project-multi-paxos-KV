package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class NACK implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 114L;

    public final int ts;
    public final int t;

    public NACK(int ts, int t) {
        this.ts = ts;
        this.t = t;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ts", ts)
                .add("t", t)
                .toString();
    }
}
