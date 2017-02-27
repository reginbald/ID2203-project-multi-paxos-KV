package se.kth.id2203.simulation.epfd;

import se.kth.id2203.epfd.EPFD;
import se.kth.id2203.network.PerfectLinkComponent;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class EPFDParent extends ComponentDefinition {
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);

    protected final Component epfd = create(EPFD.class, Init.NONE);
    protected final Component perfectLink = create(PerfectLinkComponent.class, Init.NONE);
}
