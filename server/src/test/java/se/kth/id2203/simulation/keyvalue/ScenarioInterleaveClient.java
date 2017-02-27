package se.kth.id2203.simulation.keyvalue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.kvstore.*;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.RouteMsg;
import se.kth.id2203.simulation.SimulationResultMap;
import se.kth.id2203.simulation.SimulationResultSingleton;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.*;

public class ScenarioInterleaveClient extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(ScenarioClient.class);
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    //******* Fields ******
    private final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private final NetAddress server = config().getValue("id2203.project.bootstrap-address", NetAddress.class);
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();
    private final Map<UUID, String> pending = new TreeMap<>();
    private final Queue<Operation> queue = new LinkedList<>();
    private int counter = 1;
    private boolean seven = false;
    private boolean nine = false;

    //******* Handlers ******
    protected final Handler<Start> startHandler = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            //GET Not found
            GetOperation get = new GetOperation("1");
            RouteMsg rm = new RouteMsg(get.key, get);
            trigger(new Message(self, server, rm), net);
            pending.put(get.id, "0");
            res.put("0", "SENT");

            //CAS Not found
            queue.add(new CasOperation("1", "1", "1"));

            // PUT OK
            queue.add(new PutOperation("1", "1"));

            // GET OK value: 1
            queue.add(new GetOperation("1"));

            //CAS OK
            queue.add(new CasOperation("1", "1", "5"));

            //CAS NO_MATCH
            queue.add(new CasOperation("1", "1", "3"));

            // GET OK value: 5
            queue.add(new GetOperation("1"));

            // PUT OK
            queue.add(new PutOperation("3", "1"));

            // PUT OK
            queue.add(new PutOperation("2", "1"));

            // GET OK value: 1
            queue.add(new GetOperation("3"));

            // GET OK value: 1
            queue.add(new GetOperation("2"));

        }
    };
    protected final ClassMatchedHandler<OpResponse, Message> responseHandler = new ClassMatchedHandler<OpResponse, Message>() {

        @Override
        public void handle(OpResponse content, Message context) {
        LOG.debug("Got OpResponse: {}", content);
        String key = pending.remove(content.id);
        if (key != null) {
            res.put(key, "Status: " + content.status.toString() + " Data: "+ content.data);
        } else {
            LOG.warn("ID {} was not pending! Ignoring response.", content.id);
        }
        if(key.equals("7")) seven = true;
        if(key.equals("9")) nine = true;
        if (seven && counter == 7){
            PutOperation put = (PutOperation) queue.remove();
            RouteMsg rm = new RouteMsg(put.key, put.value, put);
            trigger(new Message(self, server, rm), net);
            pending.put(put.id, "" + counter);
            res.put("" + counter, "SENT");
            counter++;

            put = (PutOperation) queue.remove();
            rm = new RouteMsg(put.key, put.value, put);
            trigger(new Message(self, server, rm), net);
            pending.put(put.id, "" + counter);
            res.put("" + counter, "SENT");
            counter++;
            return;
        }
        if (nine && counter == 9){
            GetOperation get = (GetOperation) queue.remove();
            RouteMsg rm = new RouteMsg(get.key, get);
            trigger(new Message(self, server, rm), net);
            pending.put(get.id, ""+ counter);
            res.put("" + counter, "SENT");
            counter++;

            get = (GetOperation) queue.remove();
            rm = new RouteMsg(get.key, get);
            trigger(new Message(self, server, rm), net);
            pending.put(get.id, ""+ counter);
            res.put("" + counter, "SENT");
            counter++;
            return;
        }
        if(!queue.isEmpty()){
            Operation op = queue.remove();
            if (op.getClass().equals(GetOperation.class)){
                GetOperation get = (GetOperation) op;
                RouteMsg rm = new RouteMsg(get.key, get);
                trigger(new Message(self, server, rm), net);
                pending.put(get.id, ""+ counter);
                res.put("" + counter, "SENT");
                counter++;
            } else if (op.getClass().equals(PutOperation.class)) {
                PutOperation put = (PutOperation) op;
                RouteMsg rm = new RouteMsg(put.key, put.value, put);
                trigger(new Message(self, server, rm), net);
                pending.put(put.id, "" + counter);
                res.put("" + counter, "SENT");
                counter++;
            } else if (op.getClass().equals(CasOperation.class)) {
                CasOperation cas = (CasOperation) op;
                RouteMsg rm = new RouteMsg(cas.key, cas.referenceValue, cas.newValue, cas);
                trigger(new Message(self, server, rm), net);
                pending.put(cas.id, "" + counter);
                res.put("" + counter, "SENT");
                counter++;
            }
        }
        }
    };

    {
        subscribe(startHandler, control);
        subscribe(responseHandler, net);
    }

}