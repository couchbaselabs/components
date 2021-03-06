/*
 * Copyright (c) 2017 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.talend.components.couchbase.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.couchbase.client.dcp.transport.netty.ChannelFlowController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.dcp.Client;
import com.couchbase.client.dcp.ControlEventHandler;
import com.couchbase.client.dcp.DataEventHandler;
import com.couchbase.client.dcp.StreamFrom;
import com.couchbase.client.dcp.StreamTo;
import com.couchbase.client.dcp.config.DcpControl;
import com.couchbase.client.deps.io.netty.buffer.ByteBuf;

public class CouchbaseStreamingConnection {
    private transient static final Logger LOG = LoggerFactory.getLogger(CouchbaseStreamingConnection.class);
    private static AtomicInteger threadId = new AtomicInteger(0);

    private final Client client;
    private volatile boolean connected;
    private volatile boolean streaming;
    private volatile BlockingQueue<Event> resultsQueue;

    public CouchbaseStreamingConnection(String bootstrapNodes, String bucket, String username, String password) {
        connected = false;
        streaming = false;
        client = Client.configure()
                .connectTimeout(10000L)
                .hostnames(bootstrapNodes)
                .username(username)
                .bucket(bucket)
                .password(password == null ? "" : password)
                .controlParam(DcpControl.Names.CONNECTION_BUFFER_SIZE, 20480)
                .bufferAckWatermark(60)
                .build();
        client.controlEventHandler(new ControlEventHandler() {
            @Override
            public void onEvent(ChannelFlowController flowController, ByteBuf event) {
                flowController.ack(event);
                event.release();
            }
        });
        client.dataEventHandler(new DataEventHandler() {
            @Override
            public void onEvent(ChannelFlowController flowController, ByteBuf event) {
                if (resultsQueue != null) {
                    try {
                        resultsQueue.put(new Event(event, flowController));
                    } catch (InterruptedException e) {
                        LOG.error("Unable to put DCP request into the results queue");
                    }
                } else {
                    flowController.ack(event);
                    event.release();
                }
            }
        });
    }

    public void connect() {
        client.connect().await(); // FIXME: use timeout from properties
        connected = true;
    }

    public void disconnect() {
        if (connected) {
            client.disconnect().await();
            connected = false;
        }
    }

    public boolean isClosed() {
        return !connected;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public void startStreaming(final BlockingQueue<Event> resultsQueue) {
        if (streaming) {
            LOG.warn("This connection already in streaming mode, create another one.");
            return;
        }
        streaming = true;
        this.resultsQueue = resultsQueue;
        client.initializeState(StreamFrom.BEGINNING, StreamTo.NOW).await();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.startStreaming(partitionsToStream()).await();
                    while (true) {
                        if (client.sessionState().isAtEnd()) {
                            break;
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                } finally {
                    streaming = false;
                }
            }
        }, "CouchbaseStreaming-" + threadId.incrementAndGet())
                .start();
    }

    public void stopStreaming() {
        if (resultsQueue != null) {
            client.stopStreaming(partitionsToStream()).await();
            BlockingQueue<Event> queue = resultsQueue;
            resultsQueue = null;
            List<Event> drained = new ArrayList<Event>();
            queue.drainTo(drained);
            for (Event event : drained) {
                event.ack();
            }
            client.disconnect();
        }
    }
    private Short[] partitionsToStream() {
        Short[] partitions = new Short[1024];
        for (short i = 0; i < 1024; i++) {
            partitions[i] = i;
        }
        return partitions;
    }
}
