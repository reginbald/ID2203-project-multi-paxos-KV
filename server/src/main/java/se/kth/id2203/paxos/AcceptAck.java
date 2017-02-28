package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class AcceptAck implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 112L;

    public final int ptsPrime;
    public final int l;
    public final int tPrime;

    public AcceptAck(int ptsPrime, int l, int tPrime) {
        this.ptsPrime = ptsPrime;
        this.l = l;
        this.tPrime = tPrime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ptsPrime", ptsPrime)
                .add("l", l)
                .add("tPrime", tPrime)
                .toString();
    }
}
