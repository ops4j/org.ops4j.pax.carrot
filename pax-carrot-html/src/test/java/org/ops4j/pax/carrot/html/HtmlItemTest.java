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

package org.ops4j.pax.carrot.html;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.ops4j.pax.carrot.api.Item;


public class HtmlItemTest {

    @Test
    public void parseTwoByTwoTable() {
        String html = "<html><table><tr><td>one</td><td>two</td></tr>"
                + "<tr><td>three</td><td>four</td></tr></table></html>";

        Document document = Jsoup.parse(html);
        Element table = document.select("table").get(0);

        Item item = new HtmlItem(table, "tr", "td");
        assertThat(item, is(notNullValue()));
        assertThat(item.hasChildren(), is(true));
        assertThat(item.hasSiblings(), is(false));
        assertThat(item.at(0), is(item));

        Item firstRow = item.at(0, 0);
        assertThat(firstRow, is(notNullValue()));
        assertThat(firstRow.hasChildren(), is(true));
        assertThat(firstRow.hasSiblings(), is(true));
        assertThat(firstRow.numSiblings(), is(1));

        Item secondRow = item.at(0, 1);
        assertThat(secondRow, is(notNullValue()));
        assertThat(secondRow.hasChildren(), is(true));
        assertThat(secondRow.hasSiblings(), is(false));
        assertThat(firstRow.nextSibling(), is(secondRow));

        Item one = item.at(0, 0, 0);
        assertThat(one.text(), is("one"));
        assertThat(firstRow.at(0, 0), is(one));

        Item two = item.at(0, 0, 1);
        assertThat(two.text(), is("two"));
        assertThat(firstRow.at(0, 1), is(two));

        Item three = item.at(0, 1, 0);
        assertThat(three.text(), is("three"));
        assertThat(secondRow.at(0, 0), is(three));

        Item four = item.at(0, 1, 1);
        assertThat(four.text(), is("four"));
        assertThat(secondRow.at(0, 1), is(four));
    }

    @Test
    public void editTwoByTwoTable() {
        String html = "<html><table><tr><td>one</td><td>two</td></tr>"
                + "<tr><td>three</td><td>four</td></tr></table></html>";

        Document document = Jsoup.parse(html);
        Element table = document.select("table").get(0);

        Item item = new HtmlItem(table, "tr", "td");
        item.at(0, 0, 0).text("five");
        item.at(0, 0, 1).text("six");
        item.at(0, 1, 0).text("seven");
        item.at(0, 1, 1).text("eight");

        assertThat(item.at(0, 0, 0).text(), is("five"));
        assertThat(item.at(0, 0, 1).text(), is("six"));
        assertThat(item.at(0, 1, 0).text(), is("seven"));
        assertThat(item.at(0, 1, 1).text(), is("eight"));
    }

    @Test
    public void checkEncodedText() {
        String html = "<html><table><tr><td>fünf</td><td>sechs</td></tr></table></html>";

        Document document = Jsoup.parse(html);
        Element table = document.select("table").get(0);

        Item item = new HtmlItem(table, "tr", "td");

        assertThat(item.at(0, 0, 0).text(), is("fünf"));
        assertThat(item.at(0, 0, 1).text(), is("sechs"));
    }

    @Test
    public void whitespaceShouldNotBeTrimmed() {
        String html = "<html><table><tr><td>eins      zwei</td></tr></table></html>";

        Document document = Jsoup.parse(html);
        Element table = document.select("table").get(0);

        Item item = new HtmlItem(table, "tr", "td");

        assertThat(item.at(0, 0, 0).text(), is("eins      zwei"));
    }
}
