## Generating required Jar files from Salesforce WSDL files

When changing to a new Salesforce version, the Jar files corresponding to the WSDL file
needs to be regenerated.

The general instructions are here:

[https://developer.salesforce.com/page/Introduction_to_the_Force.com_Web_Services_Connector](https://developer.salesforce.com/page/Introduction_to_the_Force.com_Web_Services_Connector)

We only need the Partner WSDL file. Check this file into source control at this directory.

When generating the Jar files from the WSDL, the command in the instructions is missing some Jar files. Below is a command that worked. You may need to Google around to find the right
Jar files to satisfy the dependencies if they have not fixed the problems.

```
java -classpath /Users/fupton/.m2/repository/org/antlr/antlr-runtime/3.5.2/antlr-runtime-3.5.2.jar:/Users/fupton/.m2/repository/com/force/api/force-wsc/35.0.0/force-wsc-35.0.0.jar:js-1.7R2.jar:ST-4.0.1.jar com.sforce.ws.tools.wsdlc partner_34.wsdl partner.jar
```

The Jar files are added to a Nexus repository for consumption by the component using
this command:

```
mvn deploy:deploy-file -Dfile=partner_34.jar -DgroupId=org.talend.components.salesforce -DartifactId=partner -Dversion=34.0.0 -Dpackaging=jar -Durl=https://artifacts-zl.talend.com/nexus/content/repositories/releases -DrepositoryId=talend_nexus_deployment
```

Make sure the pom.xml for components-salesforce is updated with the correct version number based on the new version:

        <dependency>
            <groupId>org.talend.components.salesforce</groupId>
            <artifactId>partner</artifactId>
            <version>34.0.0</version>
        </dependency>
