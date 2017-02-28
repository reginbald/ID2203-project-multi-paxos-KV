package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.LinkedList;

public class Accept implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 111L;

    public final int t;
    public final int pts;
    public final LinkedList<KompicsEvent> v;
    public final int pv_length;

    public Accept(int pts, LinkedList<KompicsEvent> v, int pv_length, int t) {
        this.t = t;
        this.pts = pts;
        this.pv_length = pv_length;
        this.v = v;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pts", pts)
                .add("v", v)
                .add("pv_length", pv_length)
                .add("t", t)
                .toString();
    }
}
