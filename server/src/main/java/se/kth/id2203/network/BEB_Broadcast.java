package se.kth.id2203.network;

import se.sics.kompics.KompicsEvent;

public class BEB_Broadcast implements KompicsEvent {
    public final KompicsEvent payload;

    BEB_Broadcast(KompicsEvent payload){
        this.payload = payload;
    }
}
