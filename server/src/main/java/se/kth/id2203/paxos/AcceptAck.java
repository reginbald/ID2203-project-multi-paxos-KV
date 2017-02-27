package se.kth.id2203.paxos;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class AcceptAck implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 112L;
    //pts,suffix(pv,l),l,t
}
