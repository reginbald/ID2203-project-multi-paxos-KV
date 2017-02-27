package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Prepare implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 115L;

    public final int timestamp;
    public final int acceptor_seq_length;
    public final int proposer_timestamp;

    public Prepare(int timestamp, int acceptor_seq_length, int proposer_timestamp) {
        this.timestamp = timestamp;
        this.acceptor_seq_length = acceptor_seq_length;
        this.proposer_timestamp = proposer_timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("acceptor_seq_length", acceptor_seq_length)
                .add("proposer_timestamp", proposer_timestamp)
                .toString();
    }
}
