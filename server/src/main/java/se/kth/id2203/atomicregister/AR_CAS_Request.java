package se.kth.id2203.atomicregister;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class AR_CAS_Request implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1111111111111111111L;

    public final UUID request_id;
    public final NetAddress request_source;

    public final String key;


    public final String referenceValue;
    public final String newValue;

    public AR_CAS_Request(UUID id, NetAddress source, String key, String referenceValue, String newValue) {
        this.request_id = id;
        this.request_source = source;

        this.key = key;
        this.referenceValue = referenceValue;
        this.newValue = newValue;

    }
}
