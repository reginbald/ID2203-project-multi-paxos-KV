package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Decide implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 113L;

    public final Object key;

    public final int timestamp;
    public final int proposer_timestamp;
    public final int proposer_seq_length;

    public Decide(Object key, int timestamp, int proposer_timestamp, int proposer_seq_length) {
        this.key = key;
        this.timestamp = timestamp;
        this.proposer_timestamp = proposer_timestamp;
        this.proposer_seq_length = proposer_seq_length;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("timestamp", timestamp)
                .add("proposer_timestamp", proposer_timestamp)
                .add("proposer_seq_length", proposer_seq_length)
                .toString();
    }
}