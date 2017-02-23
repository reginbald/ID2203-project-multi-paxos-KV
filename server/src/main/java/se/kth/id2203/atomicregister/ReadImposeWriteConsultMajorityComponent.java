package se.kth.id2203.atomicregister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.network.*;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Address;

import java.util.HashMap;

public class ReadImposeWriteConsultMajorityComponent extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(ReadImposeWriteConsultMajorityComponent.class);

    protected final Negative<AtomicRegister> nnar = provides(AtomicRegister.class);

    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    protected final Positive<PerfectLink> pLink = requires(PerfectLink.class);

    int ts = 0;
    int wr = 0;
    Object value = null;
    int acks = 0;
    Object readval = null;
    Object writeval = null;
    int rid = 0;
    HashMap<Address, Tuple> readlist = new HashMap<>();
    boolean reading = false;

    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    int n;
    int selfRank;

    public ReadImposeWriteConsultMajorityComponent() {
        subscribe(initHandler, boot);
        subscribe(ar_read_requestHandler, nnar);
        subscribe(ar_write_requestHandler, nnar);
        subscribe(beb_deliver_readHandler, beb);
        subscribe(beb_deliver_writeHandler, beb);
        subscribe(pl_deliver_valueHandler, pLink);
        subscribe(pl_deliver_ackHandler, pLink);
    }

    protected final Handler<Partition> initHandler = new Handler<Partition>(){
        @Override
        public void handle(Partition partition) {
            n = partition.nodes.size(); // all nodes in partition
            selfRank = self.getPort(); //Todo: find out if correct
        }
    };

    protected final Handler<AR_Read_Request> ar_read_requestHandler = new Handler<AR_Read_Request>() {
        @Override
        public void handle(AR_Read_Request readRequest) {
            LOG.info("Read Request handler");
            rid += 1;
            acks = 0;
            readlist = new HashMap<>();
            reading = true;
            trigger(new BEB_Broadcast(readRequest.request_id, readRequest.request_source, new READ(rid)), beb);
        }
    };

    protected final Handler<AR_Write_Request> ar_write_requestHandler = new Handler<AR_Write_Request>() {
        @Override
        public void handle(AR_Write_Request writeRequest) {
            LOG.info("Write Request handler");
            rid += 1;
            writeval = writeRequest.value;
            acks = 0;
            readlist = new HashMap<>();
            trigger(new BEB_Broadcast(writeRequest.request_id, writeRequest.request_source, new READ(rid)), beb);
        }
    };

    protected final ClassMatchedHandler<READ, BEB_Deliver> beb_deliver_readHandler = new ClassMatchedHandler<READ, BEB_Deliver>() {
        @Override
        public void handle(READ read, BEB_Deliver b) {
            LOG.info("BEB_Deliver handler READ");
            trigger(new PL_Send(b.request_id, b.request_source, b.source, new VALUE(read.rid, ts, wr, value)), pLink);
        }

    };

    protected final ClassMatchedHandler<WRITE, BEB_Deliver> beb_deliver_writeHandler = new ClassMatchedHandler<WRITE, BEB_Deliver>() {
        @Override
        public void handle(WRITE w, BEB_Deliver b) {
            LOG.info("BEB_Deliver handler WRITE");
            if (w.ts > ts && w.wr > wr){
                ts = w.ts;
                wr = w.wr;
                value = w.writeVal;
            }
            trigger(new PL_Send(b.request_id, b.request_source, b.source, new ACK(w.rid)), pLink);
        }
    };

    protected final ClassMatchedHandler<VALUE, PL_Deliver> pl_deliver_valueHandler = new ClassMatchedHandler<VALUE, PL_Deliver>() {
        @Override
        public void handle(VALUE v, PL_Deliver p) {
            LOG.info("PL_Deliver handler VALUE");
            if (v.rid == rid) {
                readlist.put(p.src, new Tuple(p.src, v.ts, v.wr, v.value));
                if(readlist.size() > n/2 ){
                    HashMap.Entry<Address, Tuple> maxEntry = null;
                    for (HashMap.Entry<Address, Tuple> entry : readlist.entrySet())
                    {
                        if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                        {
                            maxEntry = entry;
                        }
                    }
                    Tuple max = maxEntry.getValue();
                    readval = max.val;
                    readlist = new HashMap<>();
                    if (reading){
                        trigger(new BEB_Broadcast(p.request_id, p.request_source, new WRITE(rid, max.ts, max.wr, readval)), beb);
                    } else {
                        trigger(new BEB_Broadcast(p.request_id, p.request_source, new WRITE(rid, max.ts + 1, selfRank, writeval)), beb);
                    }
                }
            }
        }
    };

    protected final ClassMatchedHandler<ACK, PL_Deliver> pl_deliver_ackHandler = new ClassMatchedHandler<ACK, PL_Deliver>() {
        @Override
        public void handle(ACK v, PL_Deliver p) {
            LOG.info("PL_Deliver handler ACK");
            if (v.rid == rid) {
                acks += 1;
                if(acks > n/2){
                    acks = 0;
                    if (reading){
                        reading = false;
                        trigger(new AR_Read_Response(p.request_id, p.request_source, readval), nnar);
                    } else {
                        trigger(new AR_Write_Response(), nnar);
                    }
                }
            }
        }

    };

    static class Tuple implements Comparable<Tuple> {

        public final Address source;
        public final int ts;
        public final int wr;
        public final Object val;

        public Tuple(Address source, int ts, int wr, Object val) {
            this.source = source;
            this.ts = ts;
            this.wr = wr;
            this.val = val;
        }

        @Override
        public int compareTo(Tuple t) {
            int delta = this.ts - t.ts;
            if (delta == 0) {
                return wr - t.wr;
            }
            return delta;
        }
    }
}
