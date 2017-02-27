package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PrepareAck implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 116L;

    public final Object key;

    public final int timestamp;
    public final int acceptor_timestamp;
    public final LinkedList<Object> acceptor_seq;
    public final int acceptor_seq_length;
    public final int proposer_timestamp;

    public PrepareAck(Object key, int timestamp, int acceptor_timestamp, LinkedList<Object> acceptor_seq, int acceptor_seq_length, int proposer_timestamp) {
        this.key = key;
        this.timestamp = timestamp;
        this.acceptor_timestamp = acceptor_timestamp;
        this.acceptor_seq = acceptor_seq;
        this.acceptor_seq_length = acceptor_seq_length;
        this.proposer_timestamp = proposer_timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("timestamp", timestamp)
                .add("acceptor_timestamp", acceptor_timestamp)
                .add("acceptor_seq", acceptor_seq)
                .add("acceptor_seq_length", acceptor_seq_length)
                .add("proposer_timestamp", proposer_timestamp)
                .toString();
    }
}