package org.mongeez.reader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author pezet
 */
@Test
public class FilesetXMLReaderTest {

    public void testReadXmlContainsManyChangeFiles() throws Exception {
        Resource file = new ClassPathResource("mongeez_group_files.xml");
        List<Resource> files = new FilesetXMLReader().getFiles(file);
        assertEquals(files.size(), 2);
    }
}
