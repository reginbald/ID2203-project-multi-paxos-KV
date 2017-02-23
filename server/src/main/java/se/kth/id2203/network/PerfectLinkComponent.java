package se.kth.id2203.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

public class PerfectLinkComponent extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(PerfectLinkComponent.class);

    protected final Negative<PerfectLink> pLink = provides(PerfectLink.class);
    protected final Positive<Network> net = requires(Network.class);

    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);

    protected final Handler<PL_Send> pl_sendHandler = new Handler<PL_Send>() {
        @Override
        public void handle(PL_Send p) {
            //LOG.info("Send handler - to: {}, from: {}, data: {}", p.dest, self, p.payload);
            trigger(new Message(self, p.dest, new PL_Deliver(self, p.payload)), net);

        }
    };


    protected final ClassMatchedHandler<PL_Deliver, Message> pl_deliver_handler = new ClassMatchedHandler<PL_Deliver, Message>() {

        @Override
        public void handle(PL_Deliver content, Message m) {
            //LOG.info("Deliver handler - to: {}, from: {}, data: {}", m.getDestination(), m.getSource(), m.payload);
            trigger(content, pLink);
        }
    };

    {
        subscribe(pl_sendHandler, pLink);
        subscribe(pl_deliver_handler, net);
    }
}
