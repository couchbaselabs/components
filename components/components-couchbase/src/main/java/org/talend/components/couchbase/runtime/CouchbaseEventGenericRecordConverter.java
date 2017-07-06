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

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.couchbase.EventSchemaField;
import org.talend.daikon.avro.converter.AbstractAvroConverter;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

import com.couchbase.client.dcp.message.DcpDeletionMessage;
import com.couchbase.client.dcp.message.DcpExpirationMessage;
import com.couchbase.client.dcp.message.DcpMutationMessage;
import com.couchbase.client.deps.io.netty.buffer.ByteBuf;
import com.couchbase.client.deps.io.netty.util.CharsetUtil;

public class CouchbaseEventGenericRecordConverter extends AbstractAvroConverter<ByteBuf, IndexedRecord> {

    public CouchbaseEventGenericRecordConverter(Schema schema) {
        super(ByteBuf.class, schema);
    }

    private static byte[] bufToBytes(ByteBuf buf) {
        byte[] bytes;
        bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

    private static String bufToString(ByteBuf buf) {
        return new String(bufToBytes(buf), CharsetUtil.UTF_8);
    }

    @Override
    public Class<ByteBuf> getDatumClass() {
        return ByteBuf.class;
    }

    @Override
    public ByteBuf convertToDatum(IndexedRecord value) {
        throw new IndexedRecordConverter.UnmodifiableAdapterException();
    }

    @Override
    public IndexedRecord convertToAvro(ByteBuf value) {
        return new EventIndexedRecord(value);
    }

    private class EventIndexedRecord implements IndexedRecord {
        private final String key;
        private final long seqno;
        private final String event;
        private final short partition;
        private final long cas;
        private final long revSeqno;
        private final int expiration;
        private final int flags;
        private final int lockTime;
        private final byte[] content;

        public EventIndexedRecord(ByteBuf value) {
            if (DcpMutationMessage.is(value)) {
                key = bufToString(DcpMutationMessage.key(value));
                seqno = DcpMutationMessage.bySeqno(value);
                event = "mutation";
                partition = DcpMutationMessage.partition(value);
                cas = DcpMutationMessage.cas(value);
                revSeqno = DcpMutationMessage.revisionSeqno(value);
                expiration = DcpMutationMessage.expiry(value);
                flags = DcpMutationMessage.flags(value);
                lockTime = DcpMutationMessage.lockTime(value);
                content = bufToBytes(DcpMutationMessage.content(value));
            } else if (DcpDeletionMessage.is(value)) {
                key = bufToString(DcpDeletionMessage.key(value));
                seqno = DcpDeletionMessage.bySeqno(value);
                event = "deletion";
                partition = DcpDeletionMessage.partition(value);
                cas = DcpDeletionMessage.cas(value);
                revSeqno = DcpDeletionMessage.revisionSeqno(value);
                expiration = 0;
                flags = 0;
                lockTime = 0;
                content = null;
            } else if (DcpExpirationMessage.is(value)) {
                key = bufToString(DcpExpirationMessage.key(value));
                seqno = DcpExpirationMessage.bySeqno(value);
                event = "expiration";
                partition = DcpExpirationMessage.partition(value);
                cas = DcpExpirationMessage.cas(value);
                revSeqno = DcpExpirationMessage.revisionSeqno(value);
                expiration = 0;
                flags = 0;
                lockTime = 0;
                content = null;
            } else {
                throw new IllegalArgumentException("Unexpected value type: " + value.getByte(1));
            }
        }

        @Override
        public Object get(int i) {
            switch (i) {
                case EventSchemaField.EVENT_IDX:
                    return event;
                case EventSchemaField.PARTITION_IDX:
                    return partition;
                case EventSchemaField.KEY_IDX:
                    return key;
                case EventSchemaField.CAS_IDX:
                    return cas;
                case EventSchemaField.SEQNO_IDX:
                    return seqno;
                case EventSchemaField.REV_SEQNO_IDX:
                    return revSeqno;
                case EventSchemaField.EXPIRATION_IDX:
                    return expiration;
                case EventSchemaField.FLAGS_IDX:
                    return flags;
                case EventSchemaField.LOCK_TIME_IDX:
                    return lockTime;
                case EventSchemaField.CONTENT_IDX:
                    return content;
                default:
                    throw new IndexOutOfBoundsException("index argument should be > 0 and < 10");
            }
        }

        @Override
        public void put(int i, Object v) {
            throw new IndexedRecordConverter.UnmodifiableAdapterException();
        }

        @Override
        public Schema getSchema() {
            return CouchbaseEventGenericRecordConverter.this.getSchema();
        }
    }
}
