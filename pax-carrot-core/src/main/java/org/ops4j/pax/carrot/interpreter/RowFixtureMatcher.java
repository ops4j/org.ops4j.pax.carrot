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

package org.ops4j.pax.carrot.interpreter;

import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.jsoup.helper.StringUtil;
import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.fixture.Fixture;
import org.ops4j.pax.carrot.fixture.MatchedRow;
import org.ops4j.pax.carrot.invocation.Invocation;
import org.ops4j.pax.carrot.step.Step;

/**
 * Matches rows from a specification table against query results returned by a fixture.
 * 
 * @author Harald Wellmann
 * 
 */
public class RowFixtureMatcher {

    private List<MatchedRow> matches = new ArrayList<MatchedRow>();

    private List<Item> missing = new ArrayList<Item>();

    private List<Fixture> surplus = new ArrayList<Fixture>();

    /**
     * 
     * @param rows sibling list of rows to be matched
     * @param adapters
     * @param headers sibling list of header cells
     */
    protected void matchAllRows(Item rows, List<Fixture> adapters, Item headers) {
        surplus.addAll(adapters);
        for (Item row = rows; row != null; row = row.nextSibling()) {
            Fixture adapter = findMatchingFixture(row, surplus, headers);
            if (adapter != null) {
                surplus.remove(adapter);
                matches.add(new MatchedRow(row, adapter));
            }
            else {
                missing.add(row);
            }
        }
    }

    private Fixture findMatchingFixture(Item row, List<Fixture> fixtures, Item headers) {
        for (Fixture fixture : fixtures) {
            if (compare(fixture, row.firstChild(), headers)) {
                return fixture;
            }
        }
        return null;
    }

    private boolean compare(Fixture fixture, Item cells, Item headers) {
        Item header = headers;
        for (Item cell = cells; cell != null && header != null; cell = cell.nextSibling(), header = header
                .nextSibling()) {

            if (StringUtil.isBlank(cell.text())) {
                continue;
            }

            String property = header.text();
            try {
                Invocation invocation = fixture.deferredGet(property);

                Matcher<String> expectation = is(cell.text());
                Step step = new Step(invocation);
                Object actual = step.execute();
                if (!expectation.matches(actual.toString())) {
                    return false;
                }
            }
            // CHECKSTYLE:SKIP
            catch (Exception exc) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the matching rows, each associated with the matching fixture.
     * 
     * @return matching rows
     */
    public List<MatchedRow> getMatches() {
        return matches;
    }

    /**
     * Returns the missing rows that do not have a matching fixture.
     * 
     * @return rows without matching fixture
     */
    public List<Item> getMissing() {
        return missing;
    }

    /**
     * Returns the surplus fixtures that do not have a matching row.
     * 
     * @return fixtures without matching rows
     */
    public List<Fixture> getSurplus() {
        return surplus;
    }
}
