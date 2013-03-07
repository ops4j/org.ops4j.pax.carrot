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

import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Specification;

public class HtmlSpecification extends HtmlItem implements Specification {

    private Document document;

    public HtmlSpecification(Document document) {
        super(document, "table", "tr", "td");
        this.document = document;
    }
    
    @Override
    public Iterator<Item> iterator() {
        final Elements elements = document.select("table");
        return new Iterator<Item>() {
            
            Iterator<Element> it = elements.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Item next() {
                Element elem = it.next();
                return new HtmlItem(elem, "tr", "td");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }            
        };
    }
}
