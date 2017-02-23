package se.kth.id2203.atomicregister;

import se.kth.id2203.kvstore.OpResponse;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class AR_CAS_Response implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1111111111111111112L;

    public final UUID request_id;
    public final NetAddress request_source;
    public final OpResponse.Code code;

    public AR_CAS_Response(UUID id, NetAddress source, OpResponse.Code code) {
        this.request_id = id;
        this.request_source = source;

        this.code = code;
    }

}