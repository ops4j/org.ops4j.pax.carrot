/*
 * Copyright 2013 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.carrot.html.fit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.ops4j.pax.carrot.html.DocumentProcessor;

public class FitCompatibilityProcessorTest {

    @Test
    public void adaptColumnFixture() throws IOException {
        Document document = Jsoup.parse(new File("src/test/resources/fit_column.html"), null);
        String fixtureName = document.select("table tr td").first().text();
        DocumentProcessor processor = new FitCompatibilityProcessor();
        processor.process(document);
        Element cell = document.select("table tr td").first();
        assertThat(cell.text(), is("rules for"));
        assertThat(cell.nextElementSibling(), is(notNullValue()));
        assertThat(cell.nextElementSibling().text(), is(fixtureName));
    }
}
