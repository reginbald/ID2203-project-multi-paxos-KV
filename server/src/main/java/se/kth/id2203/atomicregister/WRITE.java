package se.kth.id2203.atomicregister;

import com.google.common.base.MoreObjects;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class WRITE implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -2181045153332189199L;

    public final Integer rid;
    public final Integer ts;
    public final Integer wr;
    public final Object writeVal;

    public final UUID request_id;
    public final NetAddress request_source;
    public final String key;

    public WRITE(UUID request_id, NetAddress request_source, String key, Integer rid, Integer ts, Integer wr, Object writeVal) {
        this.request_id = request_id;
        this.request_source = request_source;
        this.key = key;

        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.writeVal = writeVal;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rid", rid)
                .add("ts", ts)
                .add("wr", wr)
                .add("writeVal", writeVal)
                .add("key", key)
                .toString();
    }
}
