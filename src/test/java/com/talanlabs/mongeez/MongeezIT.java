package com.talanlabs.mongeez;

import com.mongodb.MongoClient;
import com.talanlabs.mongeez.resource.ClassLoaderResourceAccessor;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class MongeezIT {

    private static final Optional<String> HOST = Optional.ofNullable(System.getenv("MONGO_HOST"));

    @Before
    public void before() {
        System.out.println("HOST "+HOST.orElse("192.168.99.100"));
    }

    @Test
    public void testEmpty() {
        MongoClient mongoClient = new MongoClient(HOST.orElse("192.168.99.100"));
        Mongeez mongeez = new Mongeez("it/changeset_empty.xml", new ClassLoaderResourceAccessor(), "test_empty", mongoClient);
        mongeez.dropAll();
        mongeez.update("");
    }

    @Test
    public void testSimple() {
        MongoClient mongoClient = new MongoClient(HOST.orElse("192.168.99.100"));
        Mongeez mongeez = new Mongeez("it/changeset_simple.xml", new ClassLoaderResourceAccessor(), "test_simple", mongoClient);
        mongeez.dropAll();
        mongeez.update("");

        Assertions.assertThat(mongoClient.getDatabase("test_simple").getCollection("organization").count()).isEqualTo(1);
    }

    @Test
    public void testContexts() {
        MongoClient mongoClient = new MongoClient(HOST.orElse("192.168.99.100"));
        Mongeez mongeez = new Mongeez("it/changeset_contexts.xml", new ClassLoaderResourceAccessor(), "test_contexts", mongoClient);
        mongeez.dropAll();
        mongeez.update("dev");

        Assertions.assertThat(mongoClient.getDatabase("test_contexts").getCollection("organization").count()).isEqualTo(3);
    }

    @Test
    public void testUpdate() {
        MongoClient mongoClient = new MongoClient(HOST.orElse("192.168.99.100"));
        Mongeez mongeez1 = new Mongeez("it/changeset_update1.xml", new ClassLoaderResourceAccessor(), "test_update", mongoClient);
        mongeez1.dropAll();
        mongeez1.update(null);

        Assertions.assertThat(mongoClient.getDatabase("test_update").getCollection("organization").count()).isEqualTo(1);

        Mongeez mongeez2 = new Mongeez("it/changeset_update2.xml", new ClassLoaderResourceAccessor(), "test_update", mongoClient);
        mongeez2.update(null);

        Assertions.assertThat(mongoClient.getDatabase("test_update").getCollection("organization").count()).isEqualTo(2);
    }
}
