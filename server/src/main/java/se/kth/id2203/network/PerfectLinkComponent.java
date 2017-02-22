package se.kth.id2203.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

public class PerfectLinkComponent extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(PerfectLinkComponent.class);

    protected final Positive<Network> net = requires(Network.class);
    protected final Negative<PerfectLink> pLink = provides(PerfectLink.class);

    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);

    protected final Handler<PL_Send> handler = new Handler<PL_Send>() {
        @Override
        public void handle(PL_Send p) {
            LOG.info("Send handler - to: {}, from: {}, data: {}", p.dest, self, p.payload);
            trigger(new Message(self, p.dest, p.payload), net);
        }
    };

    {
        subscribe(handler, pLink);
    }
}
