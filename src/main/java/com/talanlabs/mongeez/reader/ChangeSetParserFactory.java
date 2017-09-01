/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.talanlabs.mongeez.reader;

import com.talanlabs.mongeez.resource.ResourceAccessor;

import java.util.ArrayList;
import java.util.List;

public class ChangeSetParserFactory {

    private static ChangeSetParserFactory instance;

    private List<ChangeSetParser> parsers;

    private ChangeSetParserFactory() {
        parsers = new ArrayList<>();
        parsers.add(new FormattedJavascriptChangeSetParser());
        parsers.add(new XmlChangeSetParser());
    }

    public static synchronized ChangeSetParserFactory getInstance() {
        if (instance == null) {
            instance = new ChangeSetParserFactory();
        }
        return instance;
    }

    public static void setInstance(ChangeSetParserFactory instance) {
        ChangeSetParserFactory.instance = instance;
    }

    public ChangeSetParser getChangeSetParser(String changeLogFile, ResourceAccessor resourceAccessor) {
        for (ChangeSetParser parser : parsers) {
            if (parser.supports(changeLogFile, resourceAccessor)) return parser;
        }
        return null;
    }

    public List<ChangeSetParser> getParsers() {
        return parsers;
    }

    public void registry(ChangeSetParser parser) {
        this.parsers.add(parser);
    }

    public void unregistry(ChangeSetParser parser) {
        this.parsers.remove(parser);
    }

}
