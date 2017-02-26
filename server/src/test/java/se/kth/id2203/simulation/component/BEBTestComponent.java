package se.kth.id2203.simulation.component;

import se.kth.id2203.network.BasicBroadcast;
import se.kth.id2203.network.PerfectLink;
import se.kth.id2203.network.PerfectLinkComponent;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

public class BEBTestComponent extends ComponentDefinition {

    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    //-----------------------
    protected final Component basicb = create(BasicBroadcast.class, Init.NONE);
    protected final Component perfectLink = create(PerfectLinkComponent.class, Init.NONE);


    {
        //BasicBroadcast
        connect(perfectLink.getPositive(PerfectLink.class), basicb.getNegative(PerfectLink.class), Channel.TWO_WAY);

        //Perfect Link Component
        connect(net, perfectLink.getNegative(Network.class), Channel.TWO_WAY);
    }
}