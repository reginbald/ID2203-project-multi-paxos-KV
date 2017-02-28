package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PrepareAck implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 116L;

    public final int ts;
    public final int ats;
    public final LinkedList<KompicsEvent> vsuf;
    public final int al;
    public final int t;

    public PrepareAck(int ts, int ats, LinkedList<KompicsEvent> vsuf, int al, int t) {
        this.ts = ts;
        this.ats = ats;
        this.vsuf = vsuf;
        this.al = al;
        this.t = t;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ts", ts)
                .add("ats", ats)
                .add("vsuf", vsuf)
                .add("al", al)
                .add("t", t)
                .toString();
    }
}