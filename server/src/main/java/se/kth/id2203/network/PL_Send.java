package se.kth.id2203.network;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Address;

public class PL_Send implements KompicsEvent {
    public final Address dest;
    public final KompicsEvent payload;

    PL_Send(Address dest, KompicsEvent payload){
        this.dest = dest;
        this.payload = payload;
    }
}
