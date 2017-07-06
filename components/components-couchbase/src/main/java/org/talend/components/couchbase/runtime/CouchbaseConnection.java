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

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.RawJsonDocument;

public class CouchbaseConnection {

    private final CouchbaseCluster cluster;
    private final String bucketName;
    private final String password;
    private Bucket bucket;
    private int refCounter = 0;

    public CouchbaseConnection(String bootstrapNodes, String bucket, String password) {
        this.cluster = CouchbaseCluster.create(bootstrapNodes);
        this.bucketName = bucket;
        this.password = password;
    }

    public void connect() {
        bucket = cluster.openBucket(bucketName, password);
    }

    public void upsert(String id, String content) {
        bucket.upsert(RawJsonDocument.create(id, content));
    }

    public void increment() {
        refCounter++;
    }

    public void decrement() {
        refCounter--;
        if (refCounter == 0) {
            bucket.close();
        }
    }
}
