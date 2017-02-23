package se.kth.id2203.atomicregister;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class READ implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -8481045121332189199L;

    public final Integer rid;

    public READ(Integer rid) {
        this.rid = rid;
    }
}