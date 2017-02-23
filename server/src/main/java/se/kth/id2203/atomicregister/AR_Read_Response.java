package se.kth.id2203.atomicregister;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class AR_Read_Response implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -3481045153332189199L;
    public final Object value;
    public final NetAddress request_source;
    public final UUID request_id;

    public AR_Read_Response(UUID id, NetAddress source, Object value){
        this.request_id = id;
        this.request_source = source;
        this.value = value;
    }
}
