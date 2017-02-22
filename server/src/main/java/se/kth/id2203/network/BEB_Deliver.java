package se.kth.id2203.network;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Address;

import java.io.Serializable;

public class BEB_Deliver implements KompicsEvent, Serializable {
    public final Address source;
    public final KompicsEvent payload;

    BEB_Deliver(Address source, KompicsEvent payload){
        this.source = source;
        this.payload = payload;
    }
}
