package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Decide implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 113L;

    public Decide() {
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rid", 1)
                .toString();
    }
}