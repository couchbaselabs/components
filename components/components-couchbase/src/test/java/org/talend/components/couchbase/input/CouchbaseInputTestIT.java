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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.avro.generic.IndexedRecord;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.couchbase.RequiresCouchbaseServer;
import org.talend.components.couchbase.EventSchemaField;
import org.talend.components.couchbase.runtime.CouchbaseSource;
import org.talend.components.couchbase.runtime.CouchbaseStreamingConnection;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

@Category({RequiresCouchbaseServer.class})
public class CouchbaseInputTestIT {
    private static String bootstrapNodes;
    private static String bucketName;
    private static String password;

    private CouchbaseSource source;

    @BeforeClass
    public static void init() throws Exception {
        Properties props;
        try (InputStream is = CouchbaseInputTestIT.class.getClassLoader().getResourceAsStream("connection.properties")) {
            props = new Properties();
            props.load(is);
        }

        bootstrapNodes = System.getProperty("couchbase.bootstrapNodes");
        if (StringUtils.isEmpty(bootstrapNodes)) {
            // Bootstrap nodes not specified, get from properties
            bootstrapNodes = props.getProperty("bootstrapNodes");
        }

        bucketName = System.getProperty("couchbase.bucketName");
        if (StringUtils.isEmpty(bucketName)) {
            // Bucket name not specified, get from properties
            bucketName = props.getProperty("bucket");
        }

        password = System.getProperty("couchbase.password");
        if (password == null) { // Password can be empty, so check for null only
            // Password not specified, get from properties
            password = props.getProperty("password");
        }
    }

    @After
    public void release() {
        if (source == null) {
            return;
        }

        try {
            CouchbaseStreamingConnection conn = source.getConnection(null);
            conn.disconnect();
        } catch (ClassNotFoundException e) {
            // close quietly
        }
    }

    private void populateBucket() {
        CouchbaseCluster cluster = CouchbaseCluster.create(bootstrapNodes);
        Bucket bucket = cluster.openBucket(bucketName, password);
        assertTrue(bucket.bucketManager().flush());
        JsonDocument document = JsonDocument.create("foo", JsonObject.create().put("bar", 42));
        bucket.upsert(document, PersistTo.MASTER);
        bucket.close();

        cluster.disconnect();
    }

    @Test
    public void testReader() {
        populateBucket();

        Reader reader = null;
        try {
            CouchbaseInputDefinition definition = new CouchbaseInputDefinition();
            CouchbaseInputProperties properties = (CouchbaseInputProperties) definition.createRuntimeProperties();

            properties.bootstrapNodes.setValue(bootstrapNodes);
            properties.bucket.setValue(bucketName);
            properties.password.setValue(password);

            source = new CouchbaseSource();
            source.initialize(null, properties);
            reader = source.createReader(null);
            boolean hasRecords = reader.start();
            assertTrue(hasRecords);
            IndexedRecord row = (IndexedRecord) reader.getCurrent();
            assertNotNull(row);

            assertEquals("foo", row.get(EventSchemaField.KEY_IDX));
            assertEquals("{\"bar\":42}", new String(((byte[]) row.get(EventSchemaField.CONTENT_IDX))));

            reader.close();
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    fail(e.getMessage());
                }
            }
        }
    }
}
