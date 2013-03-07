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

package org.ops4j.pax.carrot.fixture;

import org.ops4j.pax.carrot.api.Item;

/**
 * Represents a row of a specification table matching a given fixture instance.
 * 
 * @author Harald Wellmann
 * 
 */
public class MatchedRow {

    /** Row of specification table. */
    private Item row;

    /** Matching fixture. */
    private Fixture fixture;

    /**
     * Constructs a matched row for a given table row and a given fixture.
     * 
     * @param row
     *            specification table row
     * @param fixture
     *            a fixture matching the given row
     */
    public MatchedRow(Item row, Fixture fixture) {
        this.row = row;
        this.fixture = fixture;
    }

    /**
     * @return the row
     */
    public Item getRow() {
        return row;
    }

    /**
     * @return the fixture
     */
    public Fixture getFixture() {
        return fixture;
    }
}
