package org.talend.components.couchbase.input;

import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class CouchbaseInputPropertiesTest {

    @Test
    public void testSetupLayout() {
        CouchbaseInputProperties properties = new CouchbaseInputProperties("root");

        properties.schema.init();
        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
//        assertThat(mainWidgets, hasSize(4));

        // CouchbaseProperties widgets
        Widget bucketWidget = main.getWidget("bucket");
        assertThat(bucketWidget, notNullValue());
        Widget passwordWidget = main.getWidget("password");
        assertThat(passwordWidget, notNullValue());
        Widget bootstrapNodesWidget = main.getWidget("bootstrapNodes");
        assertThat(bootstrapNodesWidget, notNullValue());

//        Widget schemaWidget = main.getWidget("schema");
//        assertThat(schemaWidget, notNullValue());
    }

}