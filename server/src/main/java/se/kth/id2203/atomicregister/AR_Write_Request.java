package se.kth.id2203.atomicregister;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class AR_Write_Request implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -4481045153332189199L;
    public final Object value;

    public final UUID request_id;
    public final String request_key;
    public final NetAddress request_source;

    public AR_Write_Request(UUID id, String key, NetAddress source, Object value){
        this.request_id = id;
        this.request_key = key;
        this.request_source = source;
        this.value = value;
    }
}