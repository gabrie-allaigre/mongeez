package com.talanlabs.mongeez;

import com.talanlabs.mongeez.commands.ChangeSet;
import com.talanlabs.mongeez.exception.MongeezException;
import com.talanlabs.mongeez.reader.FormattedJavascriptChangeSetParser;
import com.talanlabs.mongeez.resource.ClassLoaderResourceAccessor;
import com.talanlabs.mongeez.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import java.util.List;

public class FormattedJavascriptChangeSetParserTest {

    private FormattedJavascriptChangeSetParser formattedJavascriptChangeSetParser = new FormattedJavascriptChangeSetParser();

    @Test
    public void testSupports() {
        Assertions.assertThat(formattedJavascriptChangeSetParser.supports("base/change.js", new ClassLoaderResourceAccessor())).isTrue();
        Assertions.assertThat(formattedJavascriptChangeSetParser.supports("change.js", new ClassLoaderResourceAccessor())).isTrue();
        Assertions.assertThat(formattedJavascriptChangeSetParser.supports("base/change.xml", new ClassLoaderResourceAccessor())).isFalse();
        Assertions.assertThat(formattedJavascriptChangeSetParser.supports("base/change", new ClassLoaderResourceAccessor())).isFalse();
    }

    @Test
    public void testGetChangeSetsEmpty() {
        Assertions.assertThat(formattedJavascriptChangeSetParser.getChangeSets("js/changeset_empty.js", new ClassLoaderResourceAccessor())).isEmpty();
    }

    @Test
    public void testGetChangeSets() {
        Assertions.assertThat(formattedJavascriptChangeSetParser.getChangeSets("js/changeset1.js", new ClassLoaderResourceAccessor())).hasSize(2);
        Assertions.assertThat(formattedJavascriptChangeSetParser.getChangeSets("js/changeset2.js", new ClassLoaderResourceAccessor())).hasSize(3);
    }

    @Test
    public void testGetChangeSetsNotFound() {
        Assertions.assertThatThrownBy(() -> formattedJavascriptChangeSetParser.getChangeSets("js/config/changeset-notfound.js", new ClassLoaderResourceAccessor())).isExactlyInstanceOf(MongeezException.class);
    }

    @Test
    public void testGetChangeSetsAttribs() {
        List<ChangeSet> changeSets = formattedJavascriptChangeSetParser.getChangeSets("js/changeset_attribs.js", new ClassLoaderResourceAccessor());
        Assertions.assertThat(changeSets).hasSize(1).extracting("changeId", "author", "contextsStr", "failOnError", "runAlways").containsExactly(new Tuple("ChangeSet-1", "mlysaght", "dev,prod", false, true));
    }

    @Test
    public void testGetChangeSetsNotFormatted() {
        Assertions.assertThatThrownBy(() -> formattedJavascriptChangeSetParser.getChangeSets("js/changeset_notformatted.js", new ClassLoaderResourceAccessor())).isExactlyInstanceOf(ValidationException.class);
    }

}
