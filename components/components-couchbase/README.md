## Registering components in Talend Studio

Detailed steps of registering components are described on the Talend Wiki:
https://github.com/Talend/components/wiki/8.-Testing-the-component-in-Talend-Studio

Here is a brief summary. Let's assume that Studio has been extracted like this:

    $ cd $HOME/tmp
    ... download distribution archive ...
    $ unzip TOS_DI-20161216_1026-V6.3.1.zip
    $ STUDIO_ROOT=$HOME/tmp/TOS_DI-20161216_1026-V6.3.1

Now copy the bundle into `$STUDIO_ROOT/plugins`

    $ cp [COUCHBASE_COMPONENTS]/target/components-couchbase-0.1.0-SNAPSHOT-bundle.jar \
         $STUDIO_ROOT/plugins

Finally, edit `$STUDIO_ROOT/configuration/config.ini` as it described in
wiki page above. The diff should look like this:

```diff
--- config.ini~	2016-12-16 18:08:30.000000000 +0300
+++ config.ini	2017-03-24 22:41:20.000000000 +0300
@@ -5,7 +5,7 @@
 eclipse.product=org.talend.rcp.branding.tos.product
 #The following osgi.framework key is required for the p2 update feature not to override the osgi.bundles values.
 osgi.framework=file\:plugins/org.eclipse.osgi_3.10.100.v20150521-1310.jar
-osgi.bundles=org.eclipse.equinox.common@2:start,org.eclipse.update.configurator@3:start,org.eclipse.equinox.ds@2:start,org.eclipse.core.runtime@start,org.talend.maven.resolver@start,org.ops4j.pax.url.mvn@start,org.talend.components.api.service.osgi@start
+osgi.bundles=org.eclipse.equinox.common@2:start,org.eclipse.update.configurator@3:start,org.eclipse.equinox.ds@2:start,org.eclipse.core.runtime@start,org.talend.maven.resolver@start,org.ops4j.pax.url.mvn@start,org.talend.components.api.service.osgi@start,components-couchbase-0.1.0-SNAPSHOT-bundle.jar@start
 osgi.bundles.defaultStartLevel=4
 osgi.bundlefile.limit=200
 osgi.framework.extensions=org.talend.osgi.lib.loader
```

Now start the Studio, and you should be able to see new components on palette under
`Databases\Couchbase` category.
