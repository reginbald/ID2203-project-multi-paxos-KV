package se.kth.id2203.paxos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.atomicregister.ReadImposeWriteConsultMajorityComponent;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.network.PL_Deliver;
import se.kth.id2203.network.PL_Send;
import se.kth.id2203.network.Partition;
import se.kth.id2203.network.PerfectLink;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;

import java.util.*;

public class MultiPaxos extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(ReadImposeWriteConsultMajorityComponent.class);

    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Negative<AbortableSequenceConsensus> asc = provides(AbortableSequenceConsensus.class);
    protected final Positive<PerfectLink> pLink = requires(PerfectLink.class);

    private int n;
    private int t; //logical clock
    private int prepts; //acceptor: prepared timestamp
    private int ats, pts; // acceptor timestamp, proposer timestamp
    private List<Object> av, pv; // accepted seq, proposed seq
    private int al, pl; // length of decided seq, length of learned seq

    Set<NetAddress> nodes;

    private List<Object> proposedValues;
    private HashMap<NetAddress, Tuple> readlist;
    private HashMap<NetAddress, Integer> accepted; //proposer’s knowledge about length of acceptor’s longest accepted seq
    private HashMap<NetAddress, Integer> decided; //proposer’s knowledge about length of acceptor’s longest decided seq


    protected final Handler<Partition> initHandler = new Handler<Partition>(){
        @Override
        public void handle(Partition partition) {
            LOG.info("Init: {}", partition.nodes);
            n = partition.nodes.size(); // all nodes in partition
            t = 0;
            prepts = 0;
            ats = 0;
            av = new ArrayList<>();
            al = 0;
            pts = 0;
            pv = new ArrayList<>();
            pl = 0;
            proposedValues = new ArrayList<>();
            readlist = new HashMap<>();
            accepted = new HashMap<>();
            decided = new HashMap<>();
        }
    };

    protected final Handler<Propose> proposeHandler = new Handler<Propose>(){
        @Override
        public void handle(Propose p) {
            LOG.info("Propose: {}", p);
            t++;
            if(pts == 0){

            } else if(readlist.size() <= Math.floor(n/2) ){

            } else if(!pv.contains(p.value)){
                pv.add(p.value);
                for (NetAddress node : nodes) {
                    if(readlist.get(node) != null ){
                        List<Object> pVal = new ArrayList<>();
                        pVal.add(p.value);
                        trigger(new PL_Send(node, new Accept(t, pts, pVal,pv.size() - 1)), pLink);

                    }
                }

            }
        }
    };

    protected final ClassMatchedHandler<Prepare, PL_Deliver> prepareHandler = new ClassMatchedHandler<Prepare, PL_Deliver>() {
        @Override
        public void handle(Prepare p, PL_Deliver d) {
            LOG.info("Prepare: {}", p);

        }
    };

    protected final ClassMatchedHandler<NACK, PL_Deliver> nackHandler = new ClassMatchedHandler<NACK, PL_Deliver>() {
        @Override
        public void handle(NACK n, PL_Deliver d) {
            LOG.info("NACK: {}", n);

        }
    };

    protected final ClassMatchedHandler<PrepareAck, PL_Deliver> prepareAckHandler = new ClassMatchedHandler<PrepareAck, PL_Deliver>() {
        @Override
        public void handle(PrepareAck p, PL_Deliver d) {
            LOG.info("PrepareAck: {}", p);
            t = Math.max(t, p.timestamp) + 1; // TODO: is p.timestamp correct?
            //if pts′= pts then
            //pts = proposer timestamp
            if(p.proposer_timestamp == pts) {
                //readlist[q] := (ts, vsuf );
                // decided[q] := l;
                readlist.put(d.src, new Tuple(p.timestamp, p.acceptor_seq));
                // decided[q] := l;
                decided.put(d.src, p.acceptor_seq_length);
                //if #(readlist) = ⌊N/2⌋ + 1 then
                if (readlist.size() == (Math.floor(n/2)+1)) {
                    // (ts′, vsuf ′) := (0, ⟨⟩);
                    List<Object> vsufPrime = new ArrayList<>();
                    Tuple tuple = new Tuple(0, vsufPrime);
                    // for all (ts′′, vsuf ′′) ∈ readlist do
                    for(Map.Entry<NetAddress,Tuple> entry : readlist.entrySet()) {

                    }
                }
            }

        }
    };



    {
        subscribe(initHandler, boot);
        subscribe(proposeHandler, asc);
    }

    static class Tuple {

        public final int ts;
        public final List<Object> sequence;

        public Tuple(int ts, List<Object> sequence) {
            this.ts = ts;
            this.sequence = sequence;
        }
    }
}
