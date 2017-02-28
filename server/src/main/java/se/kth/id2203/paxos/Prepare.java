package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Prepare implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 115L;

    public final int t;
    public final int al;
    public final int pts;

    public Prepare(int pts, int al, int t) {
        this.t = t;
        this.al = al;
        this.pts = pts;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("t", t)
                .add("al", al)
                .add("pts", pts)
                .toString();
    }
}
