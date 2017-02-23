package se.kth.id2203.network;

import se.kth.id2203.atomicregister.VALUE;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;

import java.io.Serializable;
import java.util.UUID;

public class PL_Deliver implements KompicsEvent, Serializable, PatternExtractor<Class, KompicsEvent> {
    private static final long serialVersionUID = -3181045153332189199L;

    public final NetAddress src;
    public final KompicsEvent payload;
    public final UUID request_id;
    public final NetAddress request_source;

    public PL_Deliver(UUID id, NetAddress address, NetAddress src, KompicsEvent payload){
        this.request_id = id;
        this.request_source = address;
        this.src = src;
        this.payload = payload;
    }

    @Override
    public Class extractPattern() {
        return payload.getClass();
    }

    @Override
    public KompicsEvent extractValue() {
        return payload;
    }
}
