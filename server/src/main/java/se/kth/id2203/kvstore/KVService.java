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
import se.kth.id2203.kvstore.OpResponse.Code;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

import java.util.HashMap;

public class KVService extends ComponentDefinition {
    // Local data store
    private HashMap<String, String> store = new HashMap<>();

    final static Logger LOG = LoggerFactory.getLogger(KVService.class);
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Routing> route = requires(Routing.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    //******* Handlers ******
    protected final ClassMatchedHandler<Operation, Message> opHandler = new ClassMatchedHandler<Operation, Message>() {

        @Override
        public void handle(Operation content, Message context) {
            LOG.info("GET request - Key: {}!", content.key);

            if (store.containsKey(content.key)){
                String data = store.get(content.key);
                LOG.info("Value: {}!", data);
                trigger(new Message(self, context.getSource(), new OpResponse(content.id, Code.OK, data)), net);
            } else {
                LOG.info("Key not found");
                trigger(new Message(self, context.getSource(), new OpResponse(content.id, Code.NOT_FOUND, "")), net);
            }
        }

    };

    protected final ClassMatchedHandler<PutOperation, Message> putHandler = new ClassMatchedHandler<PutOperation, Message>() {

        @Override
        public void handle(PutOperation content, Message context) {
            LOG.info("PUT request - Key: {} and Value: {}!", content.key, content.value);
            store.put(content.key, content.value);
            trigger(new Message(self, context.getSource(), new OpResponse(content.id, Code.OK, content.value)), net);
        }

    };

    protected final ClassMatchedHandler<CasOperation, Message> casHandler = new ClassMatchedHandler<CasOperation, Message>() {

        @Override
        public void handle(CasOperation content, Message context) {
            LOG.info("CAS request - Key: {}, ReferenceValue: {} and NewValue: {}!", content.key, content.referenceValue, content.newValue);
            if (store.containsKey(content.key)){
                String data = store.get(content.key);
                if (content.referenceValue.equals(data)){
                    LOG.info("New Value set as: {}!", data);
                    store.put(content.key, content.newValue);
                    trigger(new Message(self, context.getSource(), new OpResponse(content.id, Code.OK, content.newValue)), net);
                } else {
                    LOG.info("Reference Value: {} does not mach Old Value: {}!", content.referenceValue, data);
                    trigger(new Message(self, context.getSource(), new OpResponse(content.id, Code.NO_MATCH, data)), net);
                }
            } else {
                LOG.info("Key not found");
                trigger(new Message(self, context.getSource(), new OpResponse(content.id, Code.NOT_FOUND, "")), net);
            }
        }

    };

    {
        subscribe(opHandler, net);
        subscribe(putHandler, net);
        subscribe(casHandler, net);
    }

}
