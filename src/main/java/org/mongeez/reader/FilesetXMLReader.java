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

package org.mongeez.reader;

import org.apache.commons.digester3.Digester;
import org.mongeez.commands.ChangeFile;
import org.mongeez.commands.ChangeFileSet;
import org.mongeez.commands.GroupFile;
import org.mongeez.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class FilesetXMLReader {

    private static final Logger logger = LoggerFactory.getLogger(FilesetXMLReader.class);

    public List<Resource> getFiles(Resource file) {
        List<Resource> files = new ArrayList<>();
        try {
            Digester digester = new Digester();
            digester.setValidating(false);

            digester.addObjectCreate("groupFiles", GroupFile.class);
            digester.addSetProperties("groupFiles");

            digester.addObjectCreate("groupFiles/changeFiles", ChangeFileSet.class);
            digester.addSetProperties("groupFiles/changeFiles");
            digester.addSetNext("groupFiles/changeFiles", "add");

            digester.addObjectCreate("groupFiles/changeFiles/file", ChangeFile.class);
            digester.addSetProperties("groupFiles/changeFiles/file");
            digester.addSetNext("groupFiles/changeFiles/file", "add");


            logger.info("Parsing XML Fileset file {}", file.getFilename());
            GroupFile groupFile = digester.parse(file.getInputStream());
            if (groupFile != null){
                List<ChangeFileSet> groupFiles = groupFile.getGroupFiles();
                logger.info("Num of groupFiles found " + groupFiles.size());
                for (ChangeFileSet changeFileSet : groupFiles) {
                    if (changeFileSet != null) {
                        logger.info("Num of changefiles found " + changeFileSet.getChangeFiles().size());
                        for (ChangeFile changeFile : changeFileSet.getChangeFiles()) {
                            files.add(file.createRelative(changeFile.getPath()));
                        }
                    }
                }
            } else {
                String message = format("The file {} doesn't seem to contain a groupFiles declaration. Are you "
                        + "using the correct file to initialize Mongeez ?", file.getFilename());
                throw new ValidationException(message);
            }
        } catch (IOException e) {
        	throw new ValidationException(e);
        } catch (org.xml.sax.SAXException e) {
        	throw new ValidationException(e);
        }
        return files;
    }
}
