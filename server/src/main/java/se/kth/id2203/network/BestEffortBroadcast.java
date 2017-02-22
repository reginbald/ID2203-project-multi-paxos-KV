package se.kth.id2203.network;

import se.sics.kompics.PortType;

public class BestEffortBroadcast extends PortType {
    {
        indication(BEB_Deliver.class);
        request(BEB_Broadcast.class);
    }
}
