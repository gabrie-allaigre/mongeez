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

package com.talanlabs.mongeez.reader;

import com.talanlabs.mongeez.commands.*;
import com.talanlabs.mongeez.exception.MongeezException;
import com.talanlabs.mongeez.resource.ResourceAccessor;
import com.talanlabs.mongeez.utils.StreamUtils;
import com.talanlabs.mongeez.validation.ValidationException;
import org.apache.commons.digester3.Digester;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class XmlChangeSetParser implements ChangeSetParser {

    private static final Logger logger = LoggerFactory.getLogger(XmlChangeSetParser.class);

    private Digester digester;

    public XmlChangeSetParser() {
        super();

        digester = new Digester();

        digester.setValidating(false);

        digester.addObjectCreate("databaseChangeLog", ChangeSetList.class);
        digester.addObjectCreate("databaseChangeLog/changeSet", ChangeSet.class);
        digester.addSetProperties("databaseChangeLog/changeSet");
        digester.addSetNext("databaseChangeLog/changeSet", "add");

        digester.addObjectCreate("databaseChangeLog/include", Include.class);
        digester.addSetProperties("databaseChangeLog/include");
        digester.addSetNext("databaseChangeLog/include", "add");

        digester.addObjectCreate("databaseChangeLog/includeAll", IncludeAll.class);
        digester.addSetProperties("databaseChangeLog/includeAll");
        digester.addSetNext("databaseChangeLog/includeAll", "add");

        digester.addObjectCreate("databaseChangeLog/changeSet/script", Script.class);
        digester.addBeanPropertySetter("databaseChangeLog/changeSet/script", "body");
        digester.addSetNext("databaseChangeLog/changeSet/script", "add");
    }

    @Override
    public boolean supports(String changeLogFile, ResourceAccessor resourceAccessor) {
        return StringUtils.endsWith(changeLogFile, ".xml");
    }

    @Override
    public List<ChangeSet> getChangeSets(String changeLogFile, ResourceAccessor resourceAccessor) {
        List<ChangeSet> changeSets = new ArrayList<>();

        try {
            logger.info("Parsing XML Change Set File {}", changeLogFile);

            InputStream is = StreamUtils.singleInputStream(changeLogFile, resourceAccessor);
            if (is == null) {
                throw new MongeezException("File " + changeLogFile + " not found");
            }

            ChangeSetList changeFileSet = digester.parse(is);
            if (changeFileSet == null) {
                logger.warn("Ignoring change file {}, the parser returned null. Please check your formatting.", changeLogFile);
            } else {
                for (Object o : changeFileSet.getList()) {
                    if (o instanceof ChangeSet) {
                        changeSets.addAll(parseChangeSet(changeLogFile, resourceAccessor, (ChangeSet) o));
                    } else if (o instanceof Include) {
                        changeSets.addAll(parseInclude(changeLogFile, resourceAccessor, (Include) o));
                    } else if (o instanceof IncludeAll) {
                        changeSets.addAll(parseIncludeAll(changeLogFile, resourceAccessor, (IncludeAll) o));
                    }
                }
            }
        } catch (IOException | org.xml.sax.SAXException e) {
            throw new ValidationException(e);
        }
        return changeSets;
    }

    private List<ChangeSet> parseChangeSet(String changeLogFile, ResourceAccessor resourceAccessor, ChangeSet changeSet) {
        ChangeSetReaderUtil.populateChangeSetResourceInfo(changeSet, changeLogFile);
        return Collections.singletonList(changeSet);
    }

    private List<ChangeSet> parseInclude(String changeLogFile, ResourceAccessor resourceAccessor, Include include) {
        return include(changeLogFile, resourceAccessor, include.getFile(), include.isRelativeToChangelogFile());
    }

    private List<ChangeSet> include(String changeLogFile, ResourceAccessor resourceAccessor, String file, boolean relativeToChangelogFile) {
        if (relativeToChangelogFile) {
            if (changeLogFile.contains("/")) {
                int i = changeLogFile.lastIndexOf("/");
                file = changeLogFile.substring(0, i + 1) + file;
            }
        }

        ChangeSetParser parser = ChangeSetParserFactory.getInstance().getChangeSetParser(file, resourceAccessor);
        return parser != null ? parser.getChangeSets(file, resourceAccessor) : Collections.emptyList();
    }

    private List<ChangeSet> parseIncludeAll(String changeLogFile, ResourceAccessor resourceAccessor, IncludeAll includeAll) throws IOException {
        Set<String> unsortedResources = resourceAccessor.list(includeAll.isRelativeToChangelogFile() ? changeLogFile : null, includeAll.getPath(), true, false, true);
        SortedSet<String> resources = new TreeSet<String>(getStandardChangeLogComparator());
        resources.addAll(unsortedResources);

        List<ChangeSet> changeSets = new ArrayList<>();
        for (String path : resources) {
            changeSets.addAll(include(null, resourceAccessor, path, false));
        }
        return changeSets;
    }

    protected Comparator<String> getStandardChangeLogComparator() {
        return new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                //by ignoring WEB-INF/classes in path all changelog Files independent
                //whehther they are in a WAR or in a JAR are order following the same rule
                return o1.replace("WEB-INF/classes/", "").compareTo(o2.replace("WEB-INF/classes/", ""));
            }
        };
    }
}
