It's fork to original mongeez https://github.com/mongeez/mongeez

### What is mongeez?

mongeez allows you to manage changes of your mongo documents and propagate these changes in sync with your code changes when you perform deployments.

### Add mongeez to your project
```xml
<dependency>
    <groupId>com.talanlabs</groupId>
	<artifactId>mongeez</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

Maven repo for releases - http://repo1.maven.org/maven2

Internal versions - https://oss.sonatype.org/content/groups/public

Example file changeset-master.xml

```xml
<databaseChangeLog>
  <include fil="changeset0.xml" relativeToChangelogFile="true"/> <!-- add file -->
  <includeAll path="ref" relativeToChangelogFile="true"/> <!-- Scan all files -->
  
  <changeSet changeId="ChangeSet-2" author="mlysaght">
            <script>
                db.user.insert({ "Name" : "Admin"});
            </script>
    </changeSet>
  <changeSet changeId="ChangeSet-2" author="mlysaght" contexts="dev">
          <script>
              db.user.insert({ "Name" : "Michael Lysaght"});
          </script>
          <script>
              db.user.insert({ "Name" : "Oleksii Iepishkin"});
          </script>
  </changeSet>
  
</databaseChangeLog>
```

```java
MongoClient mongoClient = new MongoClient();
Mongeez mongeez = new Mongeez("changeset-master.xml", new ClassLoaderResourceAccessor(), "test_empty", mongoClient);
mongeez.dropAll(); // drop all database
mongeez.update("dev"); // add contexts or null
```

## License
Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
