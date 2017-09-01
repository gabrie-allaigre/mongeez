package com.talanlabs.mongeez.utils;

import com.talanlabs.mongeez.resource.ResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class StreamUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    public static InputStream singleInputStream(String path, ResourceAccessor resourceAccessor) throws IOException {
        Set<InputStream> streams = resourceAccessor.getResourcesAsStream(path);
        if (streams == null || streams.size() == 0) {
            return null;
        }
        if (streams.size() != 1) {
            if (streams.size() > 1 && path != null && path.startsWith("liquibase/parser/core/xml/") && path.endsWith(".xsd")) {
                logger.debug("Found " + streams.size() + " files that match " + path+", but choosing one at random.");
                InputStream returnStream = null;
                for (InputStream stream : streams) {
                    if (returnStream == null) {
                        returnStream = stream;
                    } else {
                        stream.close();
                    }
                }
            } else {
                for (InputStream stream : streams) {
                    stream.close();
                }
                throw new IOException("Found " + streams.size() + " files that match " + path);
            }
        }

        return streams.iterator().next();
    }
}
