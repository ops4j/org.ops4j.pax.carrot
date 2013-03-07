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

package org.ops4j.pax.carrot.column;

import org.jsoup.helper.StringUtil;
import org.ops4j.pax.carrot.fixture.Fixture;
import org.ops4j.pax.carrot.invocation.Invocation;

/**
 * A header cell containing a column name with an optional decoration, marking the column as given
 * or expected.
 * 
 * @author Harald Wellmann
 * 
 */
public class HeaderCell {

    private String text;

    /** Creates a cell containing the given text. */
    public HeaderCell(String text) {
        this.text = text.trim();
    }

    /** Return true if this cell is undecorated and thus indicates a given column. */
    public boolean isGiven() {
        return !isExpected();
    }

    /**
     * Returns true if this cell ends with {@code ()} or {@code ?}, indicating an expected column.
     * 
     * @return
     */
    public boolean isExpected() {
        return text.endsWith("()") || text.endsWith("?");
    }

    /**
     * Returns the name of the fixture property referenced by this cell. This is the cell text
     * stripped of decorations.
     * 
     * @return
     */
    public String property() {
        return text.replaceAll("[=\\?\\(\\)]", "");
    }

    /**
     * Creates a column for the given fixture, associated to this header cell.
     * 
     * @param fixture
     * @return
     * @throws Exception
     */
    public Column createColumn(Fixture fixture) throws Exception {
        if (StringUtil.isBlank(text)) {
            return new NoOpColumn();
        }

        String property = property();
        if (isExpected()) {

            Invocation invocation = null;
            if (fixture.canGet(property)) {
                invocation = fixture.deferredGet(property);
            }
            else if (fixture.hasSimpleMethod(property)) {
                invocation = fixture.deferredSimpleMethod(property);
            }
            else {
                return new NoOpColumn();
            }
            return new ExpectedColumn(invocation, property);
        }
        return new GivenColumn(fixture.deferredSet(property), property);
    }
}
