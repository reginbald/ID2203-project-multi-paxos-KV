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
    private int t = 0; //logical clock
    private int prepts = 0; //acceptor: prepared timestamp
    private int ats = 0, pts = 0; // acceptor timestamp, proposer timestamp
    private LinkedList<KompicsEvent> av =  new LinkedList<>(), pv =  new LinkedList<>(); // accepted seq, proposed seq
    private int al = 0, pl = 0; // length of decided seq, length of learned seq

    Set<NetAddress> nodes;

    List<KompicsEvent>proposedValues = new ArrayList<>();
    private HashMap<NetAddress, Tuple> readlist = new HashMap<>();
    private HashMap<NetAddress, Integer> accepted  = new HashMap<>(); //proposer’s knowledge about length of acceptor’s longest accepted seq
    private HashMap<NetAddress, Integer> decided  = new HashMap<>(); //proposer’s knowledge about length of acceptor’s longest decided seq


    protected final Handler<Partition> initHandler = new Handler<Partition>(){
        @Override
        public void handle(Partition partition) {
            LOG.info("Init: {}", partition.nodes);
            nodes = partition.nodes;
            n = partition.nodes.size(); // all nodes in partition
            selfRank = self.getIp().hashCode() + self.getPort(); //Todo: find out if correct
            //ats = 0;
            //av =
            //al = 0;
            //pts = 0;
            //pv = new LinkedList<>();
            //pl = 0;
            //proposedValues = new ArrayList<>();
            //readlist = new HashMap<>();
            //accepted = new HashMap<>();
            //decided = new HashMap<>();
        }
    };

    protected final Handler<Propose> proposeHandler = new Handler<Propose>(){
        @Override
        public void handle(Propose p) {
            LOG.info("Propose: {}", p);
            t++;
            if(pts == 0) {
                pts = t * n + selfRank;
                pv = prefix(av, al);
                pl = 0;
                proposedValues = new ArrayList<>();
                proposedValues.add(p.value);
                readlist = new HashMap<>();
                accepted = new HashMap<>();
                decided = new HashMap<>();

                for (NetAddress node : nodes) {
                    trigger(new PL_Send(node, new Prepare(pts,al,t)), pLink);
                }
            } else if(readlist.size() <= Math.floor(n/2) ){
                proposedValues.add(p.value);
            } else if(!pv.contains(p.value)){
                pv.add(p.value);
                for (NetAddress node : nodes) {
                    if(readlist.get(node) != null ){
                        LinkedList<KompicsEvent> pVal = new LinkedList<>();
                        pVal.add(p.value);
                        trigger(new PL_Send(node, new Accept(pts, pVal, pv.size(), t)), pLink);
                    }
                }

            }
        }
    };

    protected final ClassMatchedHandler<Prepare, PL_Deliver> prepareHandler = new ClassMatchedHandler<Prepare, PL_Deliver>() {
        @Override
        public void handle(Prepare p, PL_Deliver d) {
            LOG.info("Prepare: {}", p);
            t = Math.max(t, p.tPrime) + 1;
            if (p.ts < prepts){
                trigger(new PL_Send(d.src, new NACK(p.ts, t)), pLink);
            }
            else {
                prepts = p.ts;
                trigger(new PL_Send(d.src, new PrepareAck(p.ts, ats, suffix(av, p.l), al, t)), pLink);
            }
        }
    };

    protected final ClassMatchedHandler<NACK, PL_Deliver> nackHandler = new ClassMatchedHandler<NACK, PL_Deliver>() {
        @Override
        public void handle(NACK p, PL_Deliver d) {
            LOG.info("NACK: {}", n);
            t = Math.max(t, p.tPrime) + 1;
            if (p.ptsPrime == pts) {
                pts = 0;
                trigger(new Abort(), asc);
            }
        }
    };

    protected final ClassMatchedHandler<PrepareAck, PL_Deliver> prepareAckHandler = new ClassMatchedHandler<PrepareAck, PL_Deliver>() {
        @Override
        public void handle(PrepareAck p, PL_Deliver d) {
        LOG.info("PrepareAck: {}", p);
        t = Math.max(t, p.tPrime) + 1;
        if(p.ptsPrime == pts) {
            readlist.put(d.src, new Tuple(p.ts, p.vsuf));
            decided.put(d.src, p.l);
            if (readlist.size() == (Math.floor(n/2) + 1)) {
                LinkedList<KompicsEvent> vsufPrime = new LinkedList<>();
                int tsPrime = 0;
                for(Tuple entry : readlist.values()) {
                    if((tsPrime < entry.ts) || (tsPrime == entry.ts && vsufPrime.size() < entry.sequence.size())) {
                        tsPrime = entry.ts;
                        vsufPrime = entry.sequence;
                    }
                }
                pv.addAll(vsufPrime);
                for (KompicsEvent v : proposedValues) {
                    if (!pv.contains(v)){
                        pv.add(v);
                    }
                }
                for (NetAddress node : nodes) {
                    if(readlist.get(node) != null){
                        int lPrime = decided.get(node);
                        trigger(new PL_Send(node, new Accept(pts, suffix(pv, lPrime), lPrime, t)), pLink);
                    }
                }
            }
            else if (readlist.size() > (Math.floor(n/2) + 1)) {
                trigger(new PL_Send(d.src, new Accept(pts, suffix(pv, p.l), p.l, t)), pLink);
                if (pl != 0) {
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
        t = Math.max(t, p.tPrime) + 1;
        if (p.ts != prepts){
            trigger(new PL_Send(d.src, new NACK(p.ts, t)), pLink);
        } else {
            ats = p.ts;
            if (p.offs < av.size()) {
                av = prefix(av, p.offs);
            }
            av.addAll(p.vsuf);
            trigger(new PL_Send(d.src, new AcceptAck(p.ts, av.size(), t)), pLink);
        }
        }
    };

    protected final ClassMatchedHandler<AcceptAck, PL_Deliver> acceptAckHandler = new ClassMatchedHandler<AcceptAck, PL_Deliver>() {
        @Override
        public void handle(AcceptAck p, PL_Deliver d) {
            LOG.info("AcceptAck: {}", p);
            t = Math.max(t, p.tPrime) + 1;
            if (p.ptsPrime == pts){
                accepted.put(d.src, p.l);
                int nal = 0;
                for (NetAddress node : nodes) {
                    Integer ac = accepted.get(node);
                    if(ac != null && ac >= p.l){
                        nal++;
                    }
                }
                if(pl < p.l && nal > Math.floor(n/2)){
                    pl = p.l;
                    for (NetAddress node : nodes) {
                        if (readlist.get(node) != null){
                            trigger(new PL_Send(node, new Decide(pts, pl ,t)), pLink);
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
            t = Math.max(t, p.tPrime) + 1;
            if (p.ts == prepts ){
                while (al < p.l) {
                    trigger(new DECIDE_RESPONSE(av.get(al)), asc);
                    al++;
                }
            }
        }
    };

    {
        subscribe(initHandler, boot);
        subscribe(proposeHandler, asc);
        subscribe(prepareHandler, pLink);
        subscribe(nackHandler, pLink);
        subscribe(prepareAckHandler, pLink);
        subscribe(acceptHandler, pLink);
        subscribe(acceptAckHandler, pLink);
        subscribe(decideHandler, pLink);
    }

    private LinkedList<KompicsEvent> prefix(LinkedList<KompicsEvent> sequence, int length ){
        LinkedList<KompicsEvent> out = new LinkedList<>(sequence.subList(0,  length));
        return out;
    }

    private LinkedList<KompicsEvent> suffix(LinkedList<KompicsEvent> sequence, int length ){
        LinkedList<KompicsEvent> out = new LinkedList<>(sequence.subList(length,  sequence.size()));
        return out;
    }

    static class Tuple {

        public final int ts;
        public final LinkedList<KompicsEvent> sequence;

        public Tuple(int ts, LinkedList<KompicsEvent> sequence) {
            this.ts = ts;
            this.sequence = sequence;
        }
    }
}
