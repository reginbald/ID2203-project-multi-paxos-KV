package se.kth.id2203.network;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class BEB_Broadcast implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1281045153332189199L;

    public final KompicsEvent payload;
    public final UUID request_id;
    public final NetAddress request_source;

    public BEB_Broadcast(UUID id, NetAddress addr, KompicsEvent payload){
        this.request_id = id;
        this.request_source = addr;
        this.payload = payload;
    }
}
