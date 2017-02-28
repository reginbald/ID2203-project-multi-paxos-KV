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

    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    int n;
    int selfRank;
    private int t; //logical clock
    private int prepts; //acceptor: prepared timestamp
    private int ats, pts; // acceptor timestamp, proposer timestamp
    private LinkedList<Object> av, pv; // accepted seq, proposed seq
    private int al, pl; // length of decided seq, length of learned seq

    Set<NetAddress> nodes;

    List<Object>proposedValues;
    private HashMap<NetAddress, Tuple> readlist;
    private HashMap<NetAddress, Integer> accepted; //proposer’s knowledge about length of acceptor’s longest accepted seq
    private HashMap<NetAddress, Integer> decided; //proposer’s knowledge about length of acceptor’s longest decided seq


    protected final Handler<Partition> initHandler = new Handler<Partition>(){
        @Override
        public void handle(Partition partition) {
            LOG.info("Init: {}", partition.nodes);
            n = partition.nodes.size(); // all nodes in partition
            selfRank = self.getPort(); //Todo: find out if correct
            t = 0;
            prepts = 0;
            ats = 0;
            av = new LinkedList<>();
            al = 0;
            pts = 0;
            pv = new LinkedList<>();
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
                pts = t * n + selfRank;
                pv = new LinkedList<>(av);
                pv.push(al);
                pl = 0;
                proposedValues.add(p.value);
                readlist = new HashMap<>();
                accepted = new HashMap<>();
                decided = new HashMap<>();

                for (NetAddress node : nodes) {
                    trigger(new PL_Send(node, new Prepare(t, al, pts)), pLink);
                }
            } else if(readlist.size() <= Math.floor(n/2) ){
                proposedValues.add(p.value);
            } else if(!pv.contains(p.value)){
                pv.add(p.value);
                for (NetAddress node : nodes) {
                    if(readlist.get(node) != null ){
                        LinkedList<Object> pVal = new LinkedList<>();
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
            t = Math.max(t, p.timestamp) + 1;
            if (p.proposer_timestamp < prepts){
                trigger(new PL_Send(d.src, new NACK(t, p.proposer_timestamp)), pLink); // ⟨ fpl, Send | q, [Nack, ts, t] ⟩;
            }
            else {
                prepts = p.proposer_timestamp;
                LinkedList<Object> av2 = new LinkedList<>(av);
                av2.add(p.acceptor_seq_length);
                trigger(new PL_Send(d.src, new PrepareAck(t, ats, av2, al, p.proposer_timestamp)), pLink);
            }
        }
    };

    protected final ClassMatchedHandler<NACK, PL_Deliver> nackHandler = new ClassMatchedHandler<NACK, PL_Deliver>() {
        @Override
        public void handle(NACK n, PL_Deliver d) {
            LOG.info("NACK: {}", n);
            t = Math.max(t,n.timestamp) + 1;
            if (n.proposer_timestamp == pts) {
                pts = 0;
                trigger(new Abort(), asc);
            }
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
                    int tsPrime = 0;
                    Tuple primeTuple = new Tuple(tsPrime, vsufPrime);
                    // for all (ts′′, vsuf ′′) ∈ readlist do
                    for(Map.Entry<NetAddress,Tuple> entry : readlist.entrySet()) {
                        //if ts′ < ts′′ || ( ts′ = ts′′ && #(vsuf ′) < #(vsuf ′′)  then
                        Tuple entryTuple = entry.getValue();
                        if((tsPrime < entryTuple.ts) || (tsPrime == entryTuple.ts && vsufPrime.size() < entryTuple.sequence.size())) {
                            // (ts′, vsuf ′) := (ts′′, vsuf ′′);
                            primeTuple = entryTuple;
                        }
                        //pv := pv + vsuf ′;
                        pv.add(vsufPrime); // TODO: correct ?
                        //for all v ∈ proposedValues such that v ∈/ pv do
                        for (Object object : proposedValues) {
                            if(!(pv.contains(object))) {
                                //pv := pv + ⟨v⟩;
                                pv.add(object);
                            }
                        }
                        // for all p ∈ Π such that readlist[p] ̸= ⊥ do
                        for(NetAddress addr : nodes) {
                            if(readlist.containsKey(addr)) {
                                // l′ := decided[p];
                                int lPrime = decided.get(addr);
                                //trigger ⟨ fpl,Send | p,[Accept,pts,suffix(pv,l′),l′,t] ⟩;
                                LinkedList<Object> pv2 = new LinkedList<>(pv);
                                pv2.add(lPrime);
                                trigger(new PL_Send(addr, new Accept(pts,lPrime, pv2, t)), pLink);
                            }
                        }
                    }
                }
                // else if #(readlist) > ⌊N/2⌋ + 1 then
                else if (readlist.size() > (Math.floor(n/2) + 1)) {
                    //trigger ⟨ fpl,Send | q,[Accept,pts,suffix(pv,l),l,t] ⟩;
                    LinkedList<Object> pv2 = new LinkedList<>(pv);
                    pv2.add(p.acceptor_seq_length);
                    trigger(new PL_Send(d.src, new Accept(pts, p.acceptor_seq_length, pv2, t)), pLink);
                    //if pl ̸= 0 then
                    if (pl != 0) {
                        // trigger ⟨ fpl, Send | q, [Decide, pts, pl, t] ⟩;
                        trigger(new PL_Send(d.src, new Decide(pts, pl, t)), pLink);
                    }
                }
            }

        }
    };

    protected final ClassMatchedHandler<Accept, PL_Deliver> acceptHandler = new ClassMatchedHandler<Accept, PL_Deliver>() {
        @Override
        public void handle(Accept p, PL_Deliver d) {
            LOG.info("Accept: {}", p);
            t = Math.max(t,p.timestamp) + 1;
            if (p.proposer_timestamp != prepts){
                trigger(new PL_Send(d.src, new NACK(t, p.proposer_timestamp)), pLink);
            } else {
                ats = p.proposer_timestamp;
                if (p.proposer_seq_length < av.size()) {
                    av.push(p.proposer_seq_length);
                }
                av.addAll(p.acceptor_seq);
                trigger(new PL_Send(d.src, new AcceptAck(t, av.size(), p.proposer_timestamp )), pLink);
            }
        }
    };

    protected final ClassMatchedHandler<AcceptAck, PL_Deliver> acceptAckHandler = new ClassMatchedHandler<AcceptAck, PL_Deliver>() {
        @Override
        public void handle(AcceptAck p, PL_Deliver d) {
            LOG.info("AcceptAck: {}", p);
            t = Math.max(t, p.timestamp) + 1;
            if (p.proposer_timestamp == pts){
                accepted.put(d.src, p.acceptor_seq_length);
                int nal = 0;
                for (NetAddress node : nodes) {
                    if(accepted.get(node) >= p.acceptor_seq_length){
                        nal++;
                    }
                }
                if(pl < p.acceptor_seq_length && nal > Math.floor(n/2)){
                    pl = p.acceptor_seq_length;
                    for (NetAddress node : nodes) {
                        if (readlist.get(node) != null){
                            trigger(new PL_Send(node, new Decide(t,pts, pl)), pLink);
                        }
                    }
                }
            }
        }
    };

    protected final ClassMatchedHandler<Decide, PL_Deliver> decideHandler = new ClassMatchedHandler<Decide, PL_Deliver>() {
        @Override
        public void handle(Decide p, PL_Deliver d) {
            LOG.info("decide: {}", p);
            t = Math.max(t, p.timestamp) + 1;
            if (p.proposer_timestamp == prepts ){
                while (al < p.proposer_seq_length) {
                    trigger(new DECIDE_RESPONSE(av.get(al)), asc);
                    al = al + 1;
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
