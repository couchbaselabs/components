/*
 * Copyright (c) 2016 Couchbase, Inc.
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

package org.talend.components.couchbase;

public enum EventSchemaField {
    ;
    public final static int EVENT_IDX = 0;
    public final static int PARTITION_IDX = 1;
    public final static int KEY_IDX = 2;
    public final static int CAS_IDX = 3;
    public final static int SEQNO_IDX = 4;
    public final static int REV_SEQNO_IDX = 5;
    public final static int EXPIRATION_IDX = 6;
    public final static int FLAGS_IDX = 7;
    public final static int LOCK_TIME_IDX = 8;
    public final static int CONTENT_IDX = 9;
}
