package se.kth.id2203.atomicregister;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class ACK implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1481045153332189199L;

    public final Integer rid;

    public ACK(Integer rid) {
        this.rid = rid;
    }
}
