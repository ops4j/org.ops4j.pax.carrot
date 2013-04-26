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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Marker;

public class HtmlItem implements Item {

    private static HtmlMarkerRenderer renderer = new HtmlMarkerRenderer();

    private Element elem;

    private List<String> descendantTags;

    public HtmlItem(Element elem) {
        this.elem = elem;
        this.descendantTags = Collections.emptyList();
    }

    public HtmlItem(Element elem, List<String> descendantTags) {
        this.elem = elem;
        this.descendantTags = descendantTags;
    }

    public HtmlItem(Element elem, String... descendantTags) {
        this.elem = elem;
        this.descendantTags = Arrays.asList(descendantTags);
    }

    public Iterator<Item> iterator() {
        return new HtmlItemIterator(elem.children());
    }

    public String text() {
        /*
         * If this is a cell contaning plain text without inline formatting, make sure we preserve
         * whitespace.
         */
        if (elem.childNodeSize() == 1 && (elem.childNode(0) instanceof TextNode)) {
            TextNode textNode = (TextNode) elem.childNode(0);
            return textNode.getWholeText();
        }
        else {
            return elem.text();
        }
    }

    public void text(String value) {
        elem.text(value);
    }

    public boolean hasChildren() {
        return !elem.children().isEmpty();
    }

    public Item firstChild() {
        Element child = elem.child(0);
        return (child == null) ? null : wrapChild(child);
    }

    public boolean hasSiblings() {
        return elem.nextElementSibling() != null;
    }

    public Item nextSibling() {
        Element sibling = elem.nextElementSibling();
        return (sibling == null) ? null : wrap(sibling);
    }

    public Item lastSibling() {
        Element sibling = elem.lastElementSibling();
        return (sibling == null) ? null : wrap(sibling);
    }

    public int numSiblings() {
        int lastIndex = elem.lastElementSibling().elementSiblingIndex();
        int index = elem.elementSiblingIndex();
        return lastIndex - index;
    }

    public Item at(int pos) {
        int siblingIndex = pos + elem.elementSiblingIndex();
        if (siblingIndex == 0) {
            return this;
        }
        Element sibling = elem.parent().children().get(siblingIndex);
        return (sibling == null) ? null : wrap(sibling);
    }

    public Item at(int pos, int... positions) {
        int siblingIndex = pos + elem.elementSiblingIndex();
        Element sibling = null;
        if (pos == 0) {
            sibling = elem;
        }
        else {
            sibling = elem.parent().children().get(siblingIndex);
        }
        Element child = sibling;
        int level = 0;
        for (int childIndex : positions) {
            if (child == null) {
                return null;
            }
            child = child.select(descendantTags.get(level++)).get(childIndex);
        }
        return new HtmlItem(child, descendantTags.subList(positions.length, descendantTags.size()));
    }

    public Item appendSibling() {
        Element sibling = elem.parent().appendElement(elem.tagName());
        return wrap(sibling);
    }

    public Item appendChild() {
        Element child = elem.appendElement(descendantTags.get(0));
        return wrapChild(child);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elem == null) ? 0 : elem.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        HtmlItem other = (HtmlItem) obj;
        if (elem == null) {
            if (other.elem != null) {
                return false;
            }
        }
        else if (!elem.equals(other.elem)) {
            return false;
        }
        return true;
    }

    private HtmlItem wrapChild(Element element) {
        List<String> tags = descendantTags.subList(1, descendantTags.size());
        return new HtmlItem(element, tags);
    }

    private HtmlItem wrap(Element element) {
        return new HtmlItem(element, descendantTags);
    }

    public void mark(Marker marker) {
        renderer.render(marker, elem);
    }

    @Override
    public String toString() {
        return elem.toString();
    }
}
