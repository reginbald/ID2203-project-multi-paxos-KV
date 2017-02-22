package se.kth.id2203.atomicregister;


import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class VALUE implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1181045153332189199L;

    public final Integer rid;
    public final Integer ts;
    public final Integer wr;
    public final Object value;

    public VALUE(Integer rid, Integer ts, Integer wr, Object value) {
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.value = value;
    }
}