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

package org.talend.components.couchbase.input;

import static org.talend.daikon.avro.SchemaConstants.TALEND_IS_LOCKED;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.couchbase.CouchbaseProperties;
import org.talend.components.couchbase.EventSchemaField;
import org.talend.daikon.avro.AvroUtils;

public class CouchbaseInputProperties extends CouchbaseProperties {

    public CouchbaseInputProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        schema.schema.setValue(getEventSchema());
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        if (isOutputConnection) {
            return Collections.singleton(MAIN_CONNECTOR);
        }
        return Collections.emptySet();
    }

    public static Schema getEventSchema() {
        Schema.Field[] fields = new Schema.Field[10];
        Schema.Field field;

        fields[EventSchemaField.EVENT_IDX] = new Schema.Field("event", AvroUtils._string(), "Type of event", (Object) null, Schema.Field.Order.ASCENDING);
        fields[EventSchemaField.PARTITION_IDX] = new Schema.Field("partition", AvroUtils._short(), "Partition number", (Object) null, Schema.Field.Order.ASCENDING);
        fields[EventSchemaField.KEY_IDX] = new Schema.Field("key", AvroUtils._string(), "Key", (Object) null, Schema.Field.Order.ASCENDING);
        fields[EventSchemaField.CAS_IDX] = new Schema.Field("cas", AvroUtils._long(), "CAS", (Object) null, Schema.Field.Order.ASCENDING);
        fields[EventSchemaField.SEQNO_IDX] = new Schema.Field("bySeqno", AvroUtils._long(), "Sequence number", (Object) null, Schema.Field.Order.ASCENDING);
        fields[EventSchemaField.REV_SEQNO_IDX] = new Schema.Field("revSeqno", AvroUtils._long(), "Revision sequence number", (Object) null, Schema.Field.Order.ASCENDING);
        fields[EventSchemaField.EXPIRATION_IDX] = new Schema.Field("expiration", AvroUtils.wrapAsNullable(AvroUtils._int()), "Expiration", (Object) null, Schema.Field.Order.ASCENDING);
        fields[EventSchemaField.FLAGS_IDX] = new Schema.Field("flags", AvroUtils.wrapAsNullable(AvroUtils._int()), "Flags", (Object) null, Schema.Field.Order.ASCENDING);
        fields[EventSchemaField.LOCK_TIME_IDX] = new Schema.Field("lockTime", AvroUtils.wrapAsNullable(AvroUtils._int()), "Lock time", (Object) null, Schema.Field.Order.ASCENDING);
        field = new Schema.Field("content", AvroUtils.wrapAsNullable(AvroUtils._bytes()), "Content", (Object) null, Schema.Field.Order.ASCENDING);
        field.addProp(TALEND_IS_LOCKED, "false");
        fields[EventSchemaField.CONTENT_IDX] = field;

        Schema schema = Schema.createRecord("DcpMessage", "Couchbase DCP message", "com.couchbase",
                false, Arrays.asList(fields));
        schema.addProp(TALEND_IS_LOCKED, "true");

        return schema;
    }

}
