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

package org.ops4j.pax.carrot.marker;

import org.ops4j.pax.carrot.api.Marker;

/**
 * Marks an item which did not meet the interpreter's expectation.
 * 
 * @author Harald Wellmann
 *
 */
public class WrongMarker implements Marker {

    private Object actual;
    private boolean detailed;

    public void addDetails(Object actualResult) {
        this.actual = actualResult;
        this.detailed = true;
    }

    public boolean isDetailed() {
        return detailed;
    }

    public Object getActual() {
        return actual;
    }
}
