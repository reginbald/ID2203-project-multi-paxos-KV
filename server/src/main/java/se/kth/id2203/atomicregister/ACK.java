package se.kth.id2203.atomicregister;

import com.google.common.base.MoreObjects;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class ACK implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1481045153332189129L;

    public final Integer rid;

    public final UUID request_id;
    public final NetAddress request_source;
    public final String key;

    public ACK(UUID request_id, NetAddress request_source, String key, Integer rid) {
        this.request_id = request_id;
        this.request_source = request_source;
        this.key = key;
        this.rid = rid;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rid", rid)
                .add("key", key)
                .toString();
    }
}
