package se.kth.id2203.network;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class BEB_Broadcast implements KompicsEvent, Serializable {
    public final KompicsEvent payload;

    BEB_Broadcast(KompicsEvent payload){
        this.payload = payload;
    }
}
