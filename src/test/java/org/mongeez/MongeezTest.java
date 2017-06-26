/*
 * Copyright 2011 SecondMarket Labs, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.mongeez;

import com.mongodb.DB;
import com.mongodb.Mongo;
<<<<<<< HEAD
import com.mongodb.MongoClient;

import org.mongeez.validation.ValidationException;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.BeforeMethod;
=======
>>>>>>> 202fd5662ce899d3e3ca503e6627cfbc0da0e6cb
import org.testng.annotations.Test;

@Test
public class MongeezTest extends AbstractMongeezTest {

    private DB db;

    @Override
    protected Mongo prepareDatabase(String databaseName) {
        Mongo mongo = new Mongo();
        db = mongo.getDB(databaseName);
        db.dropDatabase();
        return mongo;
    }

<<<<<<< HEAD
    private Mongeez create(String path) {
        Mongeez mongeez = new Mongeez();
        mongeez.setFile(new ClassPathResource(path));
        mongeez.setMongo(mongo);
        mongeez.setDbName(dbName);
        return mongeez;
    }

    private Mongeez createWithMongoClient(String path) {
        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDB(dbName);
        db.dropDatabase();

        Mongeez mongeez = new Mongeez();
        mongeez.setFile(new ClassPathResource(path));
        mongeez.setMongoClient(mongoClient);
        mongeez.setDbName(dbName);
        return mongeez;
    }

    @Test(groups = "dao")
    public void testMongeez() throws Exception {
        Mongeez mongeez = create("mongeez.xml");

        mongeez.process();

        assertEquals(db.getCollection("mongeez").count(), 5);

        assertEquals(db.getCollection("organization").count(), 2);
        assertEquals(db.getCollection("user").count(), 2);
    }

    @Test(groups = "dao")
    public void testMongeezWithMongoClient() throws Exception {
        Mongeez mongeez = createWithMongoClient("mongeez.xml");

        mongeez.process();

        assertEquals(db.getCollection("mongeez").count(), 5);

        assertEquals(db.getCollection("organization").count(), 2);
        assertEquals(db.getCollection("user").count(), 2);
    }

    @Test(groups = "dao")
    public void testRunTwice() throws Exception {
        testMongeez();
        testMongeez();
    }

    @Test(groups = "dao")
    public void testFailOnError_False() throws Exception {
        assertEquals(db.getCollection("mongeez").count(), 0);

        Mongeez mongeez = create("mongeez_fail.xml");
        mongeez.process();

        assertEquals(db.getCollection("mongeez").count(), 2);
    }

    @Test(groups = "dao", expectedExceptions = com.mongodb.MongoCommandException.class)
    public void testFailOnError_True() throws Exception {
        Mongeez mongeez = create("mongeez_fail_fail.xml");
        mongeez.process();
    }

    @Test(groups = "dao")
    public void testNoFiles() throws Exception {
        Mongeez mongeez = create("mongeez_empty.xml");
        mongeez.process();

        assertEquals(db.getCollection("mongeez").count(), 1);
    }

    @Test(groups = "dao")
    public void testNoFailureOnEmptyChangeLog() throws Exception {
        assertEquals(db.getCollection("mongeez").count(), 0);

        Mongeez mongeez = create("mongeez_empty_changelog.xml");
        mongeez.process();

        assertEquals(db.getCollection("mongeez").count(), 1);
    }

    @Test(groups = "dao")
    public void testNoFailureOnNoChangeFilesBlock() throws Exception {
        assertEquals(db.getCollection("mongeez").count(), 0);

        Mongeez mongeez = create("mongeez_no_changefiles_declared.xml");
        mongeez.process();
        assertEquals(db.getCollection("mongeez").count(), 1);
=======
    @Override
    protected long collectionCount(String collection) {
        return db.getCollection(collection).count();
>>>>>>> 202fd5662ce899d3e3ca503e6627cfbc0da0e6cb
    }

}
