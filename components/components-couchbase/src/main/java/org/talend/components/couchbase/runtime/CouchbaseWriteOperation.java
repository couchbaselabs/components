package org.talend.components.couchbase.runtime;

import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;

import java.util.Map;

public class CouchbaseWriteOperation implements WriteOperation<Result> {

    private static final long serialVersionUID = 3100763962852785197L;

    private final CouchbaseSink sink;

    public CouchbaseWriteOperation(CouchbaseSink sink) {
        this.sink = sink;
    }

    @Override
    public void initialize(RuntimeContainer adaptor) {

    }

    @Override
    public Map<String, Object> finalize(Iterable<Result> results, RuntimeContainer adaptor) {
        return Result.accumulateAndReturnMap(results);
    }

    @Override
    public Writer<Result> createWriter(RuntimeContainer adaptor) {
        return new CouchbaseWriter(this);
    }

    @Override
    public Sink getSink() {
        return sink;
    }
}
