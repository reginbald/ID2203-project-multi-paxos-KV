package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.List;

public class PrepareAck implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 116L;

    public final int ptsPrime;
    public final int ts;
    public final List<KompicsEvent> vsuf;
    public final int l;
    public final int tPrime;

    public PrepareAck(int ptsPrime, int ts, List<KompicsEvent> vsuf, int l, int tPrime) {
        this.ptsPrime = ptsPrime;
        this.ts = ts;
        this.vsuf = vsuf;
        this.l = l;
        this.tPrime = tPrime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ptsPrime", ptsPrime)
                .add("ts", ts)
                .add("vsuf", vsuf)
                .add("l", l)
                .add("tPrime", tPrime)
                .toString();
    }
}