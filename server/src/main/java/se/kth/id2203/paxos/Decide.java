package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Decide implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 113L;

    public final int pts;
    public final int pl;
    public final int t;

    public Decide(int pts, int pl, int t) {
        this.pts = pts;
        this.pl = pl;
        this.t = t;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pts", pts)
                .add("pl", pl)
                .add("t", t)
                .toString();
    }
}