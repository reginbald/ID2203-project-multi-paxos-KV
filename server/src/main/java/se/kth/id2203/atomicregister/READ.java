package se.kth.id2203.atomicregister;

import com.google.common.base.MoreObjects;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class READ implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -8481045121332189199L;

    public final Integer rid;
    public final UUID request_id;
    public final NetAddress request_source;


    public READ(UUID request_id, NetAddress request_source, int rid) {
        this.request_id = request_id;
        this.request_source = request_source;
        this.rid = rid;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rid", rid)
                .toString();
    }
}