/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id2203.kvstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.atomicregister.*;
import se.kth.id2203.kvstore.OpResponse.Code;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.kth.id2203.paxos.*;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class KVService extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(KVService.class);
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Routing> route = requires(Routing.class);
    protected final Positive<AtomicRegister> atomicRegister = requires(AtomicRegister.class);
    protected final Positive<AbortableSequenceConsensus> asc = requires(AbortableSequenceConsensus.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);

    private HashMap<Object,Object> store = new HashMap<>();

    //******* Handlers ******
    protected final ClassMatchedHandler<GetOperation, Message> opHandler = new ClassMatchedHandler<GetOperation, Message>() {

        @Override
        public void handle(GetOperation content, Message context) {
            LOG.info("GET request - Key: {}!", content.key);

            trigger(new Propose(new AR_Read_Request(content.id, content.key, context.getSource())), asc);
            //trigger(new AR_Read_Request(content.id, content.key, context.getSource()), atomicRegister);
        }

    };

    protected final ClassMatchedHandler<PutOperation, Message> putHandler = new ClassMatchedHandler<PutOperation, Message>() {

        @Override
        public void handle(PutOperation content, Message context) {
            LOG.info("PUT request - Key: {} and Value: {}!", content.key, content.value);

            trigger(new Propose(new AR_Write_Request(content.id, content.key, context.getSource(), content.value)), asc);
            //trigger(new AR_Write_Request(content.id, content.key, context.getSource(), content.value), atomicRegister);
        }

    };

    protected final ClassMatchedHandler<CasOperation, Message> casRequestHandler = new ClassMatchedHandler<CasOperation, Message>() {

        @Override
        public void handle(CasOperation content, Message context) {
            LOG.info("CAS request - Key: {}, ReferenceValue: {} and NewValue: {}!", content.key, content.referenceValue, content.newValue);

            trigger(new Propose(new AR_CAS_Request(content.id, context.getSource(), content.key, content.referenceValue, content.newValue)), asc);
            //trigger(new AR_CAS_Request(content.id, context.getSource(), content.key, content.referenceValue, content.newValue), atomicRegister);
        }

    };


    // Used in Paxos implementation
    protected final ClassMatchedHandler<AR_Read_Request, DECIDE_RESPONSE> decideReadHandler = new ClassMatchedHandler<AR_Read_Request, DECIDE_RESPONSE>() {

        @Override
        public void handle(AR_Read_Request get, DECIDE_RESPONSE response) {
            Object value = store.get(get.request_key);

            if (value != null){
                LOG.info("Value: {}!", value);
                trigger(new Message(self, get.request_source, new OpResponse(get.request_id, Code.OK, value.toString())), net);
            } else {
                LOG.info("Key not found");
                trigger(new Message(self, get.request_source, new OpResponse(get.request_id, Code.NOT_FOUND, "")), net);
            }
        }

    };

    // Used in Paxos implementation
    protected final ClassMatchedHandler<AR_Write_Request, DECIDE_RESPONSE> decideWriteHandler = new ClassMatchedHandler<AR_Write_Request, DECIDE_RESPONSE>() {

        @Override
        public void handle(AR_Write_Request put, DECIDE_RESPONSE response) {
            store.put(put.request_key, put.value);
            trigger(new Message(self, put.request_source, new OpResponse(put.request_id, Code.OK, "")), net);
        }

    };

    // Used in Paxos implementation
    protected final ClassMatchedHandler<AR_CAS_Request, DECIDE_RESPONSE> decideCasHandler = new ClassMatchedHandler<AR_CAS_Request, DECIDE_RESPONSE>() {

        @Override
        public void handle(AR_CAS_Request cas, DECIDE_RESPONSE response) {
            Object value = store.get(cas.key);
            if (value == null){
                trigger(new Message(self, cas.request_source, new OpResponse(cas.request_id, Code.NOT_FOUND, "")), net);
            } else {
                if(value.equals(cas.referenceValue)){
                    store.put(cas.key, cas.newValue);
                    trigger(new Message(self, cas.request_source, new OpResponse(cas.request_id, Code.OK, "")), net);
                } else {
                    trigger(new Message(self, cas.request_source,new OpResponse(cas.request_id, Code.NO_MATCH, "")), net);
                }
            }
        }

    };

    // Used in Paxos implementation
    protected final Handler<Abort> abortHandler = new Handler<Abort>() {

        @Override
        public void handle(Abort abort) {
            // Todo: whut?
            LOG.info("Abort", abort);
            //trigger(new Message(self, abort.request_source,new OpResponse(cas.request_id, Code.NO_MATCH, "")), net);
        }

    };

    // Used in atomic register implementation
    protected final Handler<AR_Read_Response> readHandler = new Handler<AR_Read_Response>() {

        @Override
        public void handle(AR_Read_Response response) {
        if (response.value != null){
            LOG.info("Value: {}!", response.value);
            trigger(new Message(self, response.request_source, new OpResponse(response.request_id, Code.OK, response.value.toString())), net);
        } else {
            LOG.info("Key not found");
            trigger(new Message(self, response.request_source, new OpResponse(response.request_id, Code.NOT_FOUND, "")), net);
        }
        }
    };

    // Used in atomic register implementation
    protected final Handler<AR_Write_Response> writeHandler = new Handler<AR_Write_Response>() {

        @Override
        public void handle(AR_Write_Response response) {
            trigger(new Message(self, response.request_source, new OpResponse(response.request_id, Code.OK, "")), net);
        }

    };

    // Used in atomic register implementation
    protected final Handler<AR_CAS_Response> casResponseHandler = new Handler<AR_CAS_Response>() {
        @Override
        public void handle(AR_CAS_Response response) {
            trigger(new Message(self, response.request_source, new OpResponse(response.request_id, response.code, "")), net);
        }
    };


    {
        subscribe(readHandler, atomicRegister);
        subscribe(writeHandler, atomicRegister);
        subscribe(casResponseHandler, atomicRegister);

        subscribe(decideReadHandler, asc);
        subscribe(decideWriteHandler, asc);
        subscribe(decideCasHandler, asc);
        subscribe(abortHandler, asc);

        subscribe(opHandler, net);
        subscribe(putHandler, net);
        subscribe(casRequestHandler, net);
    }

}
