package se.kth.id2203.epfd;


import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Address;

public class Restore implements KompicsEvent {
    public final Address process;

    public Restore(Address process) {
        this.process = process;
    }
}
