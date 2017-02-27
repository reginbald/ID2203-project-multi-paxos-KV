package se.kth.id2203.paxos;

import se.sics.kompics.PortType;

public class AbortableSequenceConsensus extends PortType {
    {
        //Input to the port
        request(Propose.class);

        //Output to the port
        indication(Decide.class);
        indication(Abort.class);
    }
}