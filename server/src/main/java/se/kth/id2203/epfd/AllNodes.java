package se.kth.id2203.epfd;

import com.google.common.base.MoreObjects;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.Set;

public class AllNodes implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -111111L;

    public final Set<NetAddress> nodes;

    public AllNodes(Set<NetAddress> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodes", nodes)
                .toString();
    }
}