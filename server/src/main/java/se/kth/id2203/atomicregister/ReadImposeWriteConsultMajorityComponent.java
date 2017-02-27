package se.kth.id2203.atomicregister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.kvstore.OpResponse;
import se.kth.id2203.network.*;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Address;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ReadImposeWriteConsultMajorityComponent extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(ReadImposeWriteConsultMajorityComponent.class);

    protected final Negative<AtomicRegister> nnar = provides(AtomicRegister.class);
    protected final Positive<AtomicRegister> nnar2 = requires(AtomicRegister.class);
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    protected final Positive<PerfectLink> pLink = requires(PerfectLink.class);

    int ts = 0;
    int wr = 0;
    int acks = 0;
    int rid = 0;

    Object readval = null;
    Object writeval = null;
    Object referenceValue = null;

    // Local data store
    private HashMap<Object, Object> store = new HashMap<>();

    HashMap<Address, Tuple> readlist = new HashMap<>();

    Queue<KompicsEvent> queue = new LinkedList<>();

    boolean reading = false;
    boolean write = false;
    boolean cas = false;

    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    int n;
    int selfRank;

    public ReadImposeWriteConsultMajorityComponent() {
        subscribe(initHandler, boot);
        subscribe(ar_read_requestHandler, nnar);
        subscribe(ar_write_requestHandler, nnar);
        subscribe(ar_cas_requestHandler, nnar);
        subscribe(beb_deliver_readHandler, beb);
        subscribe(beb_deliver_writeHandler, beb);
        subscribe(pl_deliver_valueHandler, pLink);
        subscribe(pl_deliver_ackHandler, pLink);
    }

    protected final Handler<Partition> initHandler = new Handler<Partition>(){
        @Override
        public void handle(Partition partition) {
            LOG.info("Init: {}", partition.nodes);
            n = partition.nodes.size(); // all nodes in partition
            selfRank = self.getPort(); //Todo: find out if correct
        }
    };

    protected final Handler<AR_Read_Request> ar_read_requestHandler = new Handler<AR_Read_Request>() {
        @Override
        public void handle(AR_Read_Request readRequest) {
            LOG.info("Read Request handler");
            if(reading || write || cas){ // push to stack
                queue.add(readRequest);
                return;
            }

            rid += 1;
            acks = 0;
            readlist = new HashMap<>();
            reading = true;
            trigger(new BEB_Broadcast(new READ(readRequest.request_id, readRequest.request_source, readRequest.request_key, rid)), beb);
        }
    };

    protected final Handler<AR_Write_Request> ar_write_requestHandler = new Handler<AR_Write_Request>() {
        @Override
        public void handle(AR_Write_Request writeRequest) {
            LOG.info("Write Request handler");
            if(reading || write || cas){ // push to stack
                queue.add(writeRequest);
                return;
            }
            write = true;
            rid += 1;
            writeval = writeRequest.value;
            acks = 0;
            readlist = new HashMap<>();
            trigger(new BEB_Broadcast(new READ(writeRequest.request_id, writeRequest.request_source, writeRequest.request_key, rid)), beb);
        }
    };

    protected final Handler<AR_CAS_Request> ar_cas_requestHandler = new Handler<AR_CAS_Request>() {
        @Override
        public void handle(AR_CAS_Request request) {
            LOG.info("CAS Request handler");
            if(reading || write || cas){ // push to stack
                queue.add(request);
                return;
            }
            cas = true;
            rid += 1;
            writeval = request.newValue;
            referenceValue = request.referenceValue;
            acks = 0;
            readlist = new HashMap<>();
            trigger(new BEB_Broadcast(new READ(request.request_id, request.request_source, request.key, rid)), beb);
        }
    };

    protected final ClassMatchedHandler<READ, BEB_Deliver> beb_deliver_readHandler = new ClassMatchedHandler<READ, BEB_Deliver>() {
        @Override
        public void handle(READ read, BEB_Deliver b) {
            LOG.info("BEB_Deliver handler READ: {}", read);
            trigger(new PL_Send(b.source, new VALUE(read.request_id, read.request_source, read.key, read.rid, ts, wr, store.get(read.key))), pLink);
        }
    };

    protected final ClassMatchedHandler<WRITE, BEB_Deliver> beb_deliver_writeHandler = new ClassMatchedHandler<WRITE, BEB_Deliver>() {
        @Override
        public void handle(WRITE w, BEB_Deliver b) {
            LOG.info("BEB_Deliver handler WRITE: {}", w);
            if (w.ts > ts || w.wr > wr){
                ts = w.ts;
                wr = w.wr;
                store.put(w.key, w.writeVal);
            }
            LOG.info("MY STORE: {}", store);
            trigger(new PL_Send(b.source, new ACK(w.request_id, w.request_source, w.key, w.rid)), pLink);
        }
    };

    protected final ClassMatchedHandler<VALUE, PL_Deliver> pl_deliver_valueHandler = new ClassMatchedHandler<VALUE, PL_Deliver>() {
        @Override
        public void handle(VALUE v, PL_Deliver p) {
            LOG.info("PL_Deliver handler VALUE: {}", v);
            if (v.rid == rid) {
                readlist.put(p.src, new Tuple(p.src, v.ts, v.wr, v.key, v.value));
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
                        trigger(new BEB_Broadcast(new WRITE(v.request_id, v.request_source, v.key, rid, max.ts, max.wr, readval)), beb);
                    } else if(cas){
                        if (readval == null){
                            trigger(new AR_CAS_Response(v.request_id, v.request_source, OpResponse.Code.NOT_FOUND), nnar);
                            cas = false;
                            if(!queue.isEmpty()) trigger(queue.remove(), nnar2);
                        }
                        else if (referenceValue.equals(readval)){
                            trigger(new BEB_Broadcast(new WRITE(v.request_id, v.request_source, v.key, rid, max.ts + 1, selfRank, writeval)), beb);
                        } else { // reference value does not match actual value
                            trigger(new AR_CAS_Response(v.request_id, v.request_source, OpResponse.Code.NO_MATCH), nnar);
                            cas = false;
                            if(!queue.isEmpty()) trigger(queue.remove(), nnar2);
                        }
                    } else {
                        trigger(new BEB_Broadcast(new WRITE(v.request_id, v.request_source, v.key, rid, max.ts + 1, selfRank, writeval)), beb);
                    }
                }
            }
        }
    };

    protected final ClassMatchedHandler<ACK, PL_Deliver> pl_deliver_ackHandler = new ClassMatchedHandler<ACK, PL_Deliver>() {
        @Override
        public void handle(ACK v, PL_Deliver p) {
            LOG.info("PL_Deliver handler ACK: {}", v);
            if (v.rid == rid) {
                acks += 1;
                if(acks > n/2){
                    acks = 0;
                    if (reading){
                        reading = false;
                        trigger(new AR_Read_Response(v.request_id, v.request_source, readval), nnar);
                        if(!queue.isEmpty()) trigger(queue.remove(), nnar2);
                    } else if (cas) {
                        cas = false;
                        trigger(new AR_CAS_Response(v.request_id, v.request_source, OpResponse.Code.OK), nnar);
                        if(!queue.isEmpty()) trigger(queue.remove(), nnar2);
                    } else {
                        write = false;
                        trigger(new AR_Write_Response(v.request_id, v.request_source), nnar);
                        if(!queue.isEmpty()) trigger(queue.remove(), nnar2);
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

        public final Object key;

        public Tuple(Address source, int ts, int wr, Object key, Object val) {
            this.source = source;
            this.ts = ts;
            this.wr = wr;
            this.val = val;
            this.key = key;
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
