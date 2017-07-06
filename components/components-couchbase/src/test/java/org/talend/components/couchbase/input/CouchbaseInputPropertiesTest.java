package org.talend.components.couchbase.input;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

public class CouchbaseInputPropertiesTest {

    private CouchbaseInputProperties properties;

    @Before
    public void setUp() {
        properties = new CouchbaseInputProperties("root");
    }

    @Test
    public void testSetupLayout() {
        properties.init();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(4));

        // CouchbaseProperties widgets
        Widget bucketWidget = main.getWidget("bucket");
        assertThat(bucketWidget, notNullValue());
        Widget passwordWidget = main.getWidget("password");
        assertThat(passwordWidget, notNullValue());
        Widget bootstrapNodesWidget = main.getWidget("bootstrapNodes");
        assertThat(bootstrapNodesWidget, notNullValue());

        Widget schemaWidget = main.getWidget("schema");
        assertThat(schemaWidget, notNullValue());
    }

}