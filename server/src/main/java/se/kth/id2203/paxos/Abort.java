package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Abort implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 110L;

    public Abort() {
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .toString();
    }
}
