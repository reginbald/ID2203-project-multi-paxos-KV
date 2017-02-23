package se.kth.id2203.network;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.Set;

public class Partition implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -11111111111111111L;

    public final Set<NetAddress> nodes;

    public Partition(final Set<NetAddress> nodes) {
        this.nodes = nodes;
    }
}