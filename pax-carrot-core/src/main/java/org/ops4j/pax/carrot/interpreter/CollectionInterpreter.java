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
import static org.ops4j.pax.carrot.step.Actions.markItem;
import static org.ops4j.pax.carrot.step.Actions.updateStatistics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.el.ELException;

import org.jsoup.helper.StringUtil;
import org.ops4j.pax.carrot.annotation.CollectionProvider;
import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.Interpreter;
import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.column.HeaderCell;
import org.ops4j.pax.carrot.fixture.Fixture;
import org.ops4j.pax.carrot.fixture.MatchedRow;
import org.ops4j.pax.carrot.invocation.Invocation;
import org.ops4j.pax.carrot.invocation.MethodInvocation;
import org.ops4j.pax.carrot.marker.ExceptionMarker;
import org.ops4j.pax.carrot.marker.IgnoredMarker;
import org.ops4j.pax.carrot.marker.MissingMarker;
import org.ops4j.pax.carrot.marker.StoppedMarker;
import org.ops4j.pax.carrot.marker.SurplusMarker;
import org.ops4j.pax.carrot.step.Result;
import org.ops4j.pax.carrot.step.Step;

/**
 * Abstract base class for interpreting table rows as a collection of items to be matched against a
 * collection of query results returned by a fixture.
 * 
 * @author Harald Wellmann
 * 
 */
public abstract class CollectionInterpreter implements Interpreter {

    private Statistics statistics = new Statistics();

    private Fixture fixture;

    private ExecutionContext carrotContext;

    protected CollectionInterpreter(Fixture fixture) {
        this.fixture = fixture;
        this.carrotContext = fixture.getContext();
    }

    protected boolean shallProcessMissing() {
        return false;
    }

    protected boolean shallProcessSurplus() {
        return false;
    }

    public Statistics statistics() {
        return this.statistics;
    }

    protected List<?> toList(Object results) {
        if (results instanceof Object[]) {
            return Arrays.asList((Object[]) results);
        }
        if (results instanceof List) {
            return (List<?>) results;
        }
        if (results instanceof Collection) {
            return Collections.list(Collections.enumeration((Collection<?>) results));
        }
        return null;
    }

    protected void executeRow(Item valuesRow, Item headers, Fixture rowFixtureAdapter) {
        for (int i = 0; i <= valuesRow.numSiblings(); i++) {
            Item cell = valuesRow.at(i);

            if (i <= headers.numSiblings()) {
                try {
                    String property = new HeaderCell(headers.at(i).text()).property();
                    Step step = new Step(rowFixtureAdapter.deferredGet(property));
                    if (!StringUtil.isBlank(cell.text())) {
                        step.expect(is(cell.text()));
                    }
                    step.execute();
                    Result result = step.getResult();
                    markItem(cell, result, true);
                    updateStatistics(statistics, result);
                }
                // CHECKSTYLE:SKIP
                catch (Exception exc) {
                    cell.mark(new ExceptionMarker(exc));
                    statistics.exception();
                }
            }
            else {
                cell.mark(new IgnoredMarker(cell.text()));
            }
        }
    }

    protected void addSurplusRow(Item item, Item headers, Fixture rowFixtureAdapter) {
        Item row = item.appendChild();

        for (int i = 0; i <= headers.numSiblings(); i++) {
            Item cell = row.appendChild();
            try {
                String property = new HeaderCell(headers.at(i).text()).property();
                Step step = new Step(rowFixtureAdapter.deferredGet(property));

                Object actual = step.execute();

                cell.text(actual.toString());
                cell.mark(new SurplusMarker());
                statistics.wrong();
            }
            // CHECKSTYLE:SKIP
            catch (Exception exc) {
                cell.mark(new IgnoredMarker(exc));
            }
        }
    }

    protected void addMissingRow(Item row) {
        for (Item cell : row) {
            cell.mark(new MissingMarker());
            statistics.wrong();
        }
    }

    private List<?> getCollectionProvider() {
        Object target = fixture.getTarget();

        for (Method method : target.getClass().getMethods()) {
            if (method.isAnnotationPresent(CollectionProvider.class)) {
                MethodInvocation invocation = new MethodInvocation(target, method);
                return toList(invocation.invoke());
            }
        }
        return null;
    }

    public List<Fixture> getFixtureList() throws Exception {
        List<?> results = getCollectionProvider();

        if (results == null) {
            results = toList(fixture.getTarget());
        }

        if (results == null) {
            try {
                Invocation query = fixture.deferredSimpleMethod("query");
                results = toList(query.invoke());
            }
            catch (ELException exc) {
                // ignore
            }
        }

        if (results == null) {
            throw new IllegalArgumentException("cannot obtain result collection from fixture");
        }

        List<Fixture> fixtures = new ArrayList<Fixture>();
        int i = 0;
        for (Object object : results) {
            String name = String.format("fixture[%d]", i);
            fixtures.add(fixture.wrap(object, name));
            i++;
        }
        fixture.getContext().setVariable("fixture", results);
        return fixtures;
    }

    public void interpret(Item table, Statistics stats) {
        this.statistics = stats;
        try {
            List<Fixture> fixtures = getFixtureList();

            // second table row
            Item headers = table.at(0, 1);

            RowFixtureMatcher splitter = new RowFixtureMatcher();

            splitter.matchAllRows(table.at(0, 2), fixtures, headers.at(0, 0));

            for (MatchedRow rowFixture : splitter.getMatches()) {
                Item row = rowFixture.getRow();
                executeRow(row.firstChild(), headers.at(0, 0), rowFixture.getFixture());

                if (carrotContext.shouldStop(stats)) {
                    row.appendChild().mark(new StoppedMarker());
                    break;
                }
            }

            if (shallProcessMissing() && carrotContext.canContinue(stats)) {
                for (Item row : splitter.getMissing()) {
                    addMissingRow(row);

                    if (carrotContext.shouldStop(stats)) {
                        row.appendChild().mark(new StoppedMarker());
                        break;
                    }
                }
            }

            if (shallProcessSurplus() && carrotContext.canContinue(stats)) {
                for (Fixture adapter : splitter.getSurplus()) {
                    addSurplusRow(table, headers.firstChild(), adapter);

                    if (carrotContext.shouldStop(stats)) {
                        table.lastSibling().appendChild().mark(new StoppedMarker());
                        break;
                    }
                }
            }
        }
        // CHECKSTYLE:SKIP
        catch (Exception exc) {
            stats.exception();
            table.firstChild().mark(new ExceptionMarker(exc));

            if (carrotContext.shouldStop(stats)) {
                table.appendChild().mark(new StoppedMarker());
            }
        }
    }

    protected void accumulate(Statistics rowStats) {
        if (rowStats.getNumException() > 0) {
            statistics.exception();
        }
        else if (rowStats.getNumWrong() > 0) {
            statistics.wrong();
        }
        else if (rowStats.getNumRight() > 0) {
            statistics.right();
        }
        else {
            statistics.ignored();
        }
    }
}
