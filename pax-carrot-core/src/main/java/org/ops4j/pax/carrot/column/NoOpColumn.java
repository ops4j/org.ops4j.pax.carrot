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
 * A no op column which does not interact with the fixture at all. No-op column cells
 * can be used for comments.
 * 
 * @author Harald Wellmann
 *
 */
public class NoOpColumn extends Column {
    
    public NoOpColumn() {
        super("");
    }

    public Statistics processCell(Item cell) {
        return new Statistics();
    }
}
