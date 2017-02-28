package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class NACK implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 114L;

    public final int ptsPrime;
    public final int tPrime;

    public NACK(int ptsPrime, int tPrime) {
        this.ptsPrime = ptsPrime;
        this.tPrime = tPrime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ptsPrime", ptsPrime)
                .add("tPrime", tPrime)
                .toString();
    }
}
