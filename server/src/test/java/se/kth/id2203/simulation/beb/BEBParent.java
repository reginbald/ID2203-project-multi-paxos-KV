package se.kth.id2203.simulation.beb;

import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.network.*;
import se.kth.id2203.simulation.client.ScenarioBEBClient;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class BEBParent extends ComponentDefinition {

    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    //-----------------------
    protected final Component basicb = create(BasicBroadcast.class, Init.NONE);
    protected final Component perfectLink = create(PerfectLinkComponent.class, Init.NONE);
    protected final Component bebClient = create(ScenarioBEBClient.class, Init.NONE);

    {

        connect(bebClient.getPositive(Bootstrapping.class), basicb.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(basicb.getPositive(BestEffortBroadcast.class), bebClient.getNegative(BestEffortBroadcast.class), Channel.TWO_WAY);

        // connect perfectlink with basicbroadcast via perfectlink port
        connect(perfectLink.getPositive(PerfectLink.class), basicb.getNegative(PerfectLink.class), Channel.TWO_WAY);

        //two way connect perfectLink to net port
        connect(net, perfectLink.getNegative(Network.class), Channel.TWO_WAY);
        //two way connect bebClient to net port
        connect(net, bebClient.getNegative(Network.class), Channel.TWO_WAY);
    }
}