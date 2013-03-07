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

import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Statistics;

/**
 * Represents a column of a test table and encapsulates an action to be executed on cells
 * belonging to this column.
 * 
 * @author Harald Wellmann
 *
 */
public abstract class Column {

    private String property;

    
    protected Column(String property) {
        this.property = property;
    }
    
    /**
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /** Processes the cell of the current row belonging to this column. */
    public abstract Statistics processCell(Item cell);
}
