package se.kth.id2203.simulation.epfd;

import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.epfd.EPFD;
import se.kth.id2203.network.PerfectLinkComponent;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class EPFDParent extends ComponentDefinition {
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);

    protected final Component epfd = create(EPFD.class, Init.NONE);
    protected final Component perfectLink = create(PerfectLinkComponent.class, Init.NONE);
    protected final Component epfdClient = create(ScenarioEPFDClient.class, Init.NONE);

    {
        connect(epfdClient.getPositive(Bootstrapping.class), epfd.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(epfd.getPositive(Bootstrapping.class), epfdClient.getNegative(Bootstrapping.class), Channel.TWO_WAY);

        connect(net, epfdClient.getNegative(Network.class), Channel.TWO_WAY);
        connect(net, perfectLink.getNegative(Network.class), Channel.TWO_WAY);
        connect(timer, epfd.getNegative(Timer.class), Channel.TWO_WAY);
    }
}
