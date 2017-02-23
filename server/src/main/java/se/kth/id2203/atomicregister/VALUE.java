package se.kth.id2203.atomicregister;


import com.google.common.base.MoreObjects;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class VALUE implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1181045153332189199L;

    public final Integer rid;
    public final Integer ts;
    public final Integer wr;
    public final Object value;

    public final UUID request_id;
    public final NetAddress request_source;

    public VALUE(UUID request_id, NetAddress request_source, Integer rid, int ts, int wr, Object value) {
        this.request_id = request_id;
        this.request_source = request_source;
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.value = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rid", rid)
                .add("ts", ts)
                .add("wr", wr)
                .add("value", value)
                .toString();
    }
}