package se.kth.id2203.epfd;

import com.google.common.base.MoreObjects;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.Set;

public class Suspects implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -111111L;

    public final Set<NetAddress> suspects;

    public Suspects(Set<NetAddress> suspects) {
        this.suspects = suspects;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("suspects", suspects)
                .toString();
    }
}
