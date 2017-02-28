package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.List;

public class Accept implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 111L;

    public final int ts;
    public final List<KompicsEvent> vsuf;
    public final int offs;
    public final int tPrime;

    public Accept(int ts, List<KompicsEvent> vsuf, int offs, int tPrime) {
        this.ts = ts;
        this.vsuf = vsuf;
        this.offs = offs;
        this.tPrime = tPrime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ptsPrime", ts)
                .add("vsuf", vsuf)
                .add("offs", offs)
                .add("tPrime", tPrime)
                .toString();
    }

}
