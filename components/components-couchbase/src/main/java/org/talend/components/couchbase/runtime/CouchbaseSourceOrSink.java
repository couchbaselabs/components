// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.couchbase.runtime;

import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.daikon.properties.ValidationResult;

/**
 *
 */
public abstract class CouchbaseSourceOrSink implements SourceOrSink {

    protected String bootstrapNodes;
    protected String bucket;
    protected String password;

    protected static ValidationResult createValidationResult(Exception ex) {
        String message;
        if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
            message = ex.toString();
        } else {
            message = ex.getMessage();
        }
        return new ValidationResult(ValidationResult.Result.ERROR, message);
    }

}
