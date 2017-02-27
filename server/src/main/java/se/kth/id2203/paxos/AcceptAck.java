package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class AcceptAck implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 112L;

    public final Object key;

    public final int timestamp;
    public final int acceptor_seq_length;
    public final int proposer_timestamp;

    public AcceptAck(Object key, int timestamp, int acceptor_seq_length, int proposer_timestamp) {
        this.key = key;
        this.timestamp = timestamp;
        this.acceptor_seq_length = acceptor_seq_length;
        this.proposer_timestamp = proposer_timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("timestamp", timestamp)
                .add("acceptor_seq_length", acceptor_seq_length)
                .add("proposer_timestamp", proposer_timestamp)
                .toString();
    }
}
