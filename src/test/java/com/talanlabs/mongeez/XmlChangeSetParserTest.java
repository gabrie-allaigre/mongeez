package com.talanlabs.mongeez;

import com.talanlabs.mongeez.commands.ChangeSet;
import com.talanlabs.mongeez.exception.MongeezException;
import com.talanlabs.mongeez.reader.XmlChangeSetParser;
import com.talanlabs.mongeez.resource.ClassLoaderResourceAccessor;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import java.util.List;

public class XmlChangeSetParserTest {

    private XmlChangeSetParser xmlChangeSetParser = new XmlChangeSetParser();

    @Test
    public void testSupports() {
        Assertions.assertThat(xmlChangeSetParser.supports("base/change.xml", new ClassLoaderResourceAccessor())).isTrue();
        Assertions.assertThat(xmlChangeSetParser.supports("change.xml", new ClassLoaderResourceAccessor())).isTrue();
        Assertions.assertThat(xmlChangeSetParser.supports("base/change.js", new ClassLoaderResourceAccessor())).isFalse();
        Assertions.assertThat(xmlChangeSetParser.supports("base/change", new ClassLoaderResourceAccessor())).isFalse();
    }

    @Test
    public void testGetChangeSetsEmpty() {
        Assertions.assertThat(xmlChangeSetParser.getChangeSets("xml/changeset_empty.xml", new ClassLoaderResourceAccessor())).isEmpty();
    }

    @Test
    public void testGetChangeSets() {
        Assertions.assertThat(xmlChangeSetParser.getChangeSets("xml/changeset1.xml", new ClassLoaderResourceAccessor())).hasSize(2);
        Assertions.assertThat(xmlChangeSetParser.getChangeSets("xml/changeset2.xml", new ClassLoaderResourceAccessor())).hasSize(3);
    }

    @Test
    public void testGetChangeSetsInclude() {
        Assertions.assertThat(xmlChangeSetParser.getChangeSets("xml/changeset3.xml", new ClassLoaderResourceAccessor())).hasSize(5);
    }

    @Test
    public void testGetChangeSetsIncludeAll() {
        Assertions.assertThat(xmlChangeSetParser.getChangeSets("xml/config/changeset-master1.xml", new ClassLoaderResourceAccessor())).hasSize(16);
    }

    @Test
    public void testGetChangeSetsAll() {
        Assertions.assertThat(xmlChangeSetParser.getChangeSets("xml/config/changeset-master2.xml", new ClassLoaderResourceAccessor())).hasSize(14);
    }

    @Test
    public void testGetChangeSetsAttribs() {
        List<ChangeSet> changeSets = xmlChangeSetParser.getChangeSets("xml/changeset_attribs.xml", new ClassLoaderResourceAccessor());
        Assertions.assertThat(changeSets).hasSize(1).extracting("changeId", "author", "contextsStr", "failOnError", "runAlways").containsExactly(new Tuple("ChangeSet-1", "mlysaght", "dev,prod", false, true));
    }

    @Test
    public void testGetChangeSetsNotFound() {
        Assertions.assertThatThrownBy(() -> xmlChangeSetParser.getChangeSets("xml/config/changeset-notfound.xml", new ClassLoaderResourceAccessor())).isExactlyInstanceOf(MongeezException.class);
    }
}
