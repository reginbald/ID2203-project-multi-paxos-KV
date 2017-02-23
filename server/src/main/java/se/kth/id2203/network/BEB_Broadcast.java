package se.kth.id2203.network;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class BEB_Broadcast implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1281045153332189199L;

    public final KompicsEvent payload;

    public BEB_Broadcast(KompicsEvent payload){
        this.payload = payload;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("payload", payload)
                .toString();
    }
}
