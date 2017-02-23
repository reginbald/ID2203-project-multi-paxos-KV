package se.kth.id2203.atomicregister;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;
import java.io.Serializable;
import java.util.UUID;

public class AR_Read_Request implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -2481045153332189199L;

    public final UUID request_id;
    public final String request_key;
    public final NetAddress request_source;

    public AR_Read_Request(UUID id, String key, NetAddress source) {
        this.request_id = id;
        this.request_key = key;
        this.request_source = source;
    }
}
