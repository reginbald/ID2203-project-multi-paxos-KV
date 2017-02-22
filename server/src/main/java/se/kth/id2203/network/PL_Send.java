package se.kth.id2203.network;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class PL_Send implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -4181045153332189199L;

    public final NetAddress dest;
    public final KompicsEvent payload;

    public final UUID request_id;
    public final NetAddress request_source;

    public PL_Send(UUID id, NetAddress addr, NetAddress dest, KompicsEvent payload){
        this.request_id = id;
        this.request_source = addr;
        this.dest = dest;
        this.payload = payload;
    }
}
