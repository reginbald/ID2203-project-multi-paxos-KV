package se.kth.id2203;

import com.google.common.base.Optional;
import se.kth.id2203.atomicregister.AtomicRegister;
import se.kth.id2203.atomicregister.ReadImposeWriteConsultMajorityComponent;
import se.kth.id2203.bootstrapping.BootstrapClient;
import se.kth.id2203.bootstrapping.BootstrapServer;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.epfd.EPFD;
import se.kth.id2203.kvstore.KVService;
import se.kth.id2203.network.BasicBroadcast;
import se.kth.id2203.network.BestEffortBroadcast;
import se.kth.id2203.network.PerfectLink;
import se.kth.id2203.network.PerfectLinkComponent;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.kth.id2203.overlay.VSOverlayManager;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class ParentComponent
        extends ComponentDefinition {

    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    //-----------------------
    //******* Children ******
    protected final Component overlay = create(VSOverlayManager.class, Init.NONE);
    protected final Component kv = create(KVService.class, Init.NONE);
    protected final Component boot;
    //-----------------------
    protected final Component riwcmc = create(ReadImposeWriteConsultMajorityComponent.class, Init.NONE);
    protected final Component basicb = create(BasicBroadcast.class, Init.NONE);
    protected final Component perfectLink = create(PerfectLinkComponent.class, Init.NONE);
    protected final Component epfd = create(EPFD.class, Init.NONE);


    {

        Optional<NetAddress> serverO = config().readValue("id2203.project.bootstrap-address", NetAddress.class);
        if (serverO.isPresent()) { // start in client mode
            boot = create(BootstrapClient.class, Init.NONE);
        } else { // start in server mode
            boot = create(BootstrapServer.class, Init.NONE);
        }
        connect(timer, boot.getNegative(Timer.class), Channel.TWO_WAY);
        connect(net, boot.getNegative(Network.class), Channel.TWO_WAY);
        // Overlay
        connect(boot.getPositive(Bootstrapping.class), overlay.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(net, overlay.getNegative(Network.class), Channel.TWO_WAY);
        // KV
        connect(overlay.getPositive(Routing.class), kv.getNegative(Routing.class), Channel.TWO_WAY);
        connect(net, kv.getNegative(Network.class), Channel.TWO_WAY);
        connect(riwcmc.getPositive(AtomicRegister.class), kv.getNegative(AtomicRegister.class), Channel.TWO_WAY);

        // ReadImposeWriteConsultMajorityComponent
        connect(overlay.getPositive(Bootstrapping.class), riwcmc.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(basicb.getPositive(BestEffortBroadcast.class), riwcmc.getNegative(BestEffortBroadcast.class), Channel.TWO_WAY);
        connect(perfectLink.getPositive(PerfectLink.class), riwcmc.getNegative(PerfectLink.class), Channel.TWO_WAY);

        //BasicBroadcast
        connect(overlay.getPositive(Bootstrapping.class), basicb.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(perfectLink.getPositive(PerfectLink.class), basicb.getNegative(PerfectLink.class), Channel.TWO_WAY);

        //Perfect Link Component
        connect(net, perfectLink.getNegative(Network.class), Channel.TWO_WAY);

        // EPFD Component
        connect(timer, epfd.getNegative(Timer.class), Channel.TWO_WAY);
        connect(overlay.getPositive(Bootstrapping.class), epfd.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(perfectLink.getPositive(PerfectLink.class), epfd.getNegative(PerfectLink.class), Channel.TWO_WAY);

    }
}
