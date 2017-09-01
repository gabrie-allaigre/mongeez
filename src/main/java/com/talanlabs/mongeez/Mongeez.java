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

package com.talanlabs.mongeez;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.talanlabs.mongeez.commands.ChangeSet;
import com.talanlabs.mongeez.commands.Script;
import com.talanlabs.mongeez.executor.ChangeSetExecutor;
import com.talanlabs.mongeez.reader.ChangeSetParser;
import com.talanlabs.mongeez.reader.ChangeSetParserFactory;
import com.talanlabs.mongeez.resource.ResourceAccessor;
import com.talanlabs.mongeez.validation.ChangeSetsValidator;
import com.talanlabs.mongeez.validation.DefaultChangeSetsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Mongeez {

    private final static Logger logger = LoggerFactory.getLogger(Mongeez.class);

    private MongoClient mongoClient = null;
    private String dbName;
    private String changeLogFile;
    private ResourceAccessor resourceAccessor;
    private ChangeSetsValidator changeSetsValidator = new DefaultChangeSetsValidator();

    public Mongeez(String changeLogFile, ResourceAccessor resourceAccessor, String dbName, MongoClient mongoClient) {
        super();

        if (changeLogFile != null) {
            this.changeLogFile = changeLogFile.replace('\\', '/');  //convert to standard / if using absolute path on windows
        }

        this.resourceAccessor = resourceAccessor;
        this.dbName = dbName;
        this.mongoClient = mongoClient;
    }

    /**
     * Update database
     *
     * @param context context
     */
    public void update(String context) {
        List<ChangeSet> changeSets = getChangeSets();
        if (mongoClient != null) {
            new ChangeSetExecutor(mongoClient, dbName, context).execute(changeSets);
        } else {
            throw new IllegalStateException("Neither mongo nor mongoClient has been configured!");
        }
    }

    private List<ChangeSet> getChangeSets() {
        ChangeSetParserFactory readerFactory = ChangeSetParserFactory.getInstance();
        ChangeSetParser parser = readerFactory.getChangeSetParser(changeLogFile, resourceAccessor);
        List<ChangeSet> changeSets = parser.getChangeSets(changeLogFile, resourceAccessor);
        logChangeSets(changeSets);
        changeSetsValidator.validate(changeSets);
        return changeSets;
    }

    private void logChangeSets(List<ChangeSet> changeSets) {
        if (logger.isTraceEnabled()) {
            for (ChangeSet changeSet : changeSets) {
                logger.trace("Changeset");
                logger.trace("id: " + changeSet.getChangeId());
                logger.trace("author: " + changeSet.getAuthor());
                if (!"".equals(changeSet.getContexts())) {
                    logger.trace("contexts: {}", changeSet.getContexts());
                }
                for (Script command : changeSet.getCommands()) {
                    logger.trace("script");
                    logger.trace(command.getBody());
                }
            }
        }
    }

    /**
     * Drop database
     */
    public void dropAll() {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        database.drop();
    }

    public void setChangeSetsValidator(ChangeSetsValidator changeSetsValidator) {
        this.changeSetsValidator = changeSetsValidator;
    }
}
