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
    private int ats, pts;
    private LinkedList<Object> av, pv;
    private int al, pl;

    Set<NetAddress> nodes;

    private List<Object> proposedValues;
    private HashMap<NetAddress, Tuple> readlist;
    private HashMap<NetAddress, Integer> accepted;
    private HashMap<NetAddress, Integer> decided;


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
                proposedValues = new ArrayList<>();
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

        }
    };

    protected final ClassMatchedHandler<Accept, PL_Deliver> acceptHandler = new ClassMatchedHandler<Accept, PL_Deliver>() {
        @Override
        public void handle(Accept p, PL_Deliver d) {
            LOG.info("Accept: {}", p);

        }
    };

    protected final ClassMatchedHandler<AcceptAck, PL_Deliver> acceptAckHandler = new ClassMatchedHandler<AcceptAck, PL_Deliver>() {
        @Override
        public void handle(AcceptAck p, PL_Deliver d) {
            LOG.info("AcceptAck: {}", p);

        }
    };

    protected final ClassMatchedHandler<Decide, PL_Deliver> decideHandler = new ClassMatchedHandler<Decide, PL_Deliver>() {
        @Override
        public void handle(Decide p, PL_Deliver d) {
            LOG.info("AcceptAck: {}", p);

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
