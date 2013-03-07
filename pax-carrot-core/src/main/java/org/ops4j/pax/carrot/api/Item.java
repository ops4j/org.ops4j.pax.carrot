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

package org.ops4j.pax.carrot.api;

/**
 * An Item represents a part of a test specification, either a table, a row or a single cell. Items
 * build a tree stucture, with each Item having siblings and children. A table Item has row
 * children, a row Item has cell children. Cell items do not have children.
 * <p>
 * Items are Iterable, the iterator traverses the list of children.
 * <p>
 * Cell items contain text. The text() methods should not be used on non-cell items.
 * <p>
 * Items can be augmented by one or more markers, as indicated by the Markable interface. This
 * interface should not be used on non-cell items.
 * 
 * @author Harald Wellmann
 * 
 */
public interface Item extends Iterable<Item>, Markable {

    /**
     * Returns the text contained in the given item.
     * 
     * @return plain text (undefined for non-cell items)
     */
    String text();

    /**
     * Sets the text contained in the given item.
     * 
     * @param value
     *            text to be set
     */
    void text(String value);

    /**
     * Checks whether this item has any children.
     * 
     * @return
     */
    boolean hasChildren();

    /**
     * Returns the first child of this item or null.
     * 
     * @return
     */
    Item firstChild();

    /**
     * Checks whether this item has any siblings.
     * 
     * @return
     */
    boolean hasSiblings();

    /**
     * Returns the next sibling of this item, or null.
     * 
     * @return
     */
    Item nextSibling();

    /**
     * Returns the last sibling of this item, or itself.
     * 
     * @return
     */
    Item lastSibling();

    /**
     * Returns the number of remaining siblings.
     * 
     * @return
     */
    int numSiblings();

    /**
     * Returns the i-th successor sibling of this item, or null. at(0) returns the current item.
     * 
     * @param i
     * @return
     */
    Item at(int i);

    /**
     * at(i, j) returns the j-th child of the i-th successor sibling of the current item.
     * <p>
     * at(i, j, k) returns the k-th child of the j-th child of the i-th successor of the current
     * item, and so forth.
     * 
     * @param pos
     *            position in the successor sibling list of this item
     * @param positions
     *            list of positions in the child list of the item selected by the previous
     *            arguments.
     * @return selected item, or null
     */
    Item at(int pos, int... positions);

    /**
     * Appends a new item at the end of the sibling list of this item.
     * @return the new item
     */
    Item appendSibling();

    /**
     * Appends a new item at the end of the child list of this item.
     * @return the new item
     */
    Item appendChild();
}
