package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.LinkedList;

public class Accept implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 111L;

    public final Object key;

    public final int timestamp;
    public final int proposer_timestamp;
    public final LinkedList<Object> acceptor_seq;
    public final int proposer_seq_length;

    public Accept(Object key, int timestamp, int proposer_timestamp, LinkedList<Object> acceptor_seq, int proposer_seq_length) {
        this.key = key;
        this.timestamp = timestamp;
        this.acceptor_seq = acceptor_seq;
        this.proposer_seq_length = proposer_seq_length;
        this.proposer_timestamp = proposer_timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("timestamp", timestamp)
                .add("proposer_timestamp", proposer_timestamp)
                .add("acceptor_seq", acceptor_seq)
                .add("proposer_seq_length", proposer_seq_length)
                .toString();
    }
}
