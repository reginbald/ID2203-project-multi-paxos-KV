package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class AcceptAck implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 112L;

    public final int ts;
    public final int av_length;
    public final int t;

    public AcceptAck(int ts, int av_length, int t) {
        this.ts = ts;
        this.av_length = av_length;
        this.t = t;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ts", ts)
                .add("av_length", av_length)
                .add("t", t)
                .toString();
    }
}
