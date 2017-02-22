package se.kth.id2203.network;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Address;

import java.io.Serializable;

public class PL_Deliver implements KompicsEvent, Serializable {
    public final Address src;
    public final KompicsEvent payload;

    PL_Deliver(Address src, KompicsEvent payload){
        this.src = src;
        this.payload = payload;
    }
}
