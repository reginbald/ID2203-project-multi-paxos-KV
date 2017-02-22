package se.kth.id2203.atomicregister;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class WRITE implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -2181045153332189199L;

    public final Integer rid;
    public final Integer ts;
    public final Integer wr;
    public final Object writeVal;

    public WRITE(Integer rid, Integer ts, Integer wr, Object writeVal) {
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.writeVal = writeVal;
    }
}
