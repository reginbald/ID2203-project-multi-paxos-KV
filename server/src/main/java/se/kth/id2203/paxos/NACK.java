package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class NACK implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 114L;

    public final int timestamp;
    public final int proposer_timestamp;

    public NACK(int timestamp, int proposer_timestamp) {
        this.timestamp = timestamp;
        this.proposer_timestamp = proposer_timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("proposer_timestamp", proposer_timestamp)
                .toString();
    }
}
