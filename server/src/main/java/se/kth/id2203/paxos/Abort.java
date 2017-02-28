package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.List;

public class Abort implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 110L;

    public final List<KompicsEvent> values;

    public Abort(List<KompicsEvent> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("values", values)
                .toString();
    }
}
