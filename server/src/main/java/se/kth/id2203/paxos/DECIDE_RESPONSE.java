package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class DECIDE_RESPONSE implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 210L;

    public final Object value;

    public DECIDE_RESPONSE(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .toString();
    }
}
