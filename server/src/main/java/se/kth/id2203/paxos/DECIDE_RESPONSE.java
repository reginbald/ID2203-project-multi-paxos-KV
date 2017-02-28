package se.kth.id2203.paxos;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;

import java.io.Serializable;

public class DECIDE_RESPONSE implements KompicsEvent, Serializable, PatternExtractor<Class, KompicsEvent> {
    private static final long serialVersionUID = 210L;

    public final KompicsEvent value;

    public DECIDE_RESPONSE(KompicsEvent value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .toString();
    }
    @Override
    public Class extractPattern() {
        return value.getClass();
    }

    @Override
    public KompicsEvent extractValue() {
        return value;
    }
}
