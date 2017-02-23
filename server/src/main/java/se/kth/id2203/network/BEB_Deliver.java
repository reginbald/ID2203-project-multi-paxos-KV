package se.kth.id2203.network;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;

import java.io.Serializable;
import java.util.UUID;

public class BEB_Deliver implements KompicsEvent, Serializable, PatternExtractor<Class, KompicsEvent> {
    private static final long serialVersionUID = -2181045153332189199L;

    public final NetAddress source;
    public final KompicsEvent payload;

    public BEB_Deliver(NetAddress source, KompicsEvent payload){
        this.source = source;
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
