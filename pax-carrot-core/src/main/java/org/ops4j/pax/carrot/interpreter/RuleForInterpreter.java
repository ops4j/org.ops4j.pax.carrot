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

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.el.ValueExpression;

import org.ops4j.pax.carrot.annotation.AfterRow;
import org.ops4j.pax.carrot.annotation.BeforeFirstExpectation;
import org.ops4j.pax.carrot.annotation.BeforeRow;
import org.ops4j.pax.carrot.api.Interpreter;
import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.column.Column;
import org.ops4j.pax.carrot.column.ExpectedColumn;
import org.ops4j.pax.carrot.column.GivenColumn;
import org.ops4j.pax.carrot.column.HeaderCell;
import org.ops4j.pax.carrot.column.NoOpColumn;
import org.ops4j.pax.carrot.el.ELExecutionContext;
import org.ops4j.pax.carrot.fixture.Fixture;
import org.ops4j.pax.carrot.invocation.Invocation;
import org.ops4j.pax.carrot.invocation.MethodInvocation;
import org.ops4j.pax.carrot.marker.ExceptionMarker;
import org.ops4j.pax.carrot.marker.StoppedMarker;
import org.ops4j.pax.carrot.step.Actions;
import org.ops4j.pax.carrot.step.Result;
import org.ops4j.pax.carrot.step.Step;

/**
 * An interpreter which interprets the rows of a table as rules for a given fixture. Each table row
 * is an example of the specification embodied by the table. Any implementation of the specification
 * must satisfy all examples of this table.
 * <p>
 * The first table row indicates the kind of interpreter and the fixture to be used.
 * <p>
 * The second row contains column headers, indicating the fixture properties to be set or queried.
 * An undecorated property name like {@code foo} indicates a given property to be set. A property
 * name followed by a question mark or a pair of parentheses like {@code foo?} or {@code foo()}
 * indicates an expectation. All given properties must precede all exceptations, traversing the row
 * from left to right.
 * <p>
 * The interpreter executes the following actions for each table row after the second row.
 * <ul>
 * <li>Any fixture class method annotated by {@code @BeforeRow} is invoked. If there is more than
 * one such method, the order of invocation is undefined.</li>
 * 
 * <li>Traversing the row from left to right, for each cell belonging to a given column, the
 * interpreter sets the corresponding fixture property to the value contained in the cell, coercing
 * the type as required.</li>
 * 
 * <li>Any fixture class method annotated by {@code @BeforeFirstExpectation} is invoked after all
 * given columns are set and before the first expectation is queried. If there is more than one such
 * method, the order of invocation is undefined.</li>
 * 
 * <li>For each cell belonging to an expected column, the interpreter gets the corresponding fixture
 * property value and compares it to the value contained in the cell, coercing the type as required.
 * If the two values match, the cell is counted as right in the statistics of the current execution
 * context.</li>
 * 
 * <li>Any fixture class method annotated by {@code @AfterRow} is invoked after all cells of the row
 * have been traversed. If there is more than one such method, the order of invocation is undefined.
 * </li>
 * </ul>
 * 
 * @author Harald Wellmann
 * 
 */
public class RuleForInterpreter implements Interpreter {

    protected Fixture fixture;

    protected Statistics statistics;

    private Column[] columns;

    private Invocation beforeRowInvocation;

    private Invocation beforeFirstExpectationInvocation;

    private Invocation afterRowInvocation;

    private ELExecutionContext carrotContext;

    private ValueExpression filterExpr;

    private HashMap<String, String> rowMap;

    /**
     * Creates an interpreter for the given fixture.
     * 
     * @param fixture
     */
    public RuleForInterpreter(Fixture fixture) {
        this.fixture = fixture;
        this.carrotContext = (ELExecutionContext) fixture.getContext();
        this.rowMap = new HashMap<String, String>();
    }

    public void interpret(Item table, Statistics stats) {
        this.statistics = stats;
        columns = parseColumns(table);
        scanAnnotations();

        String filter = (String) carrotContext.getVariable("filter");
        carrotContext.setVariable("row", rowMap);
        if (filter != null) {
            filterExpr = carrotContext.createFilterValueExpression(filter);
        }

        for (Item row = table.at(0, 2); row != null; row = row.nextSibling()) {
            processRow(row);

            if (carrotContext.shouldStop(stats)) {
                row.firstChild().lastSibling().appendSibling().mark(new StoppedMarker());
                break;
            }
        }
    }

    /**
     * Parses the column headers of the given table.
     * 
     * @param table
     * @return
     */
    private Column[] parseColumns(Item table) {
        Item headers = table.at(0, 1, 0);
        if (headers == null) {
            return new Column[0];
        }

        columns = new Column[headers.numSiblings() + 1];
        for (int i = 0; i <= headers.numSiblings(); i++) {
            columns[i] = parseColumn(headers.at(i));
        }

        if (carrotContext.shouldStop(statistics)) {
            headers.lastSibling().appendSibling().mark(new StoppedMarker());
        }

        return columns;
    }

    /**
     * Interprets the contents of the given item as a column header.
     * 
     * @param header a table cell
     * @return
     */
    private Column parseColumn(Item header) {
        try {
            HeaderCell headerCell = new HeaderCell(header.text());
            return headerCell.createColumn(fixture);
        }
        // CHECKSTYLE:SKIP
        catch (Exception exc) {
            header.mark(new ExceptionMarker(exc));
            statistics.exception();
            return new NoOpColumn();
        }
    }

    /**
     * Interprets a table row (not including the first two rows).
     * 
     * @param row
     */
    private void processRow(Item row) {
        if (!row.hasChildren()) {
            return;
        }

        if (shouldSkipRow(row)) {
            return;
        }

        boolean firstExpectationSeen = false;
        callBeforeRow();

        Item cells = row.firstChild();
        for (int i = 0; i <= cells.numSiblings(); i++) {
            Item cell = cells.at(i);

            if (i < columns.length) {
                if (!firstExpectationSeen) {
                    if (columns[i] instanceof ExpectedColumn) {
                        callBeforeFirstExpectation();
                        firstExpectationSeen = true;
                    }
                }
                processCell(columns[i], cell);
            }
        }
        callAfterRow();
    }

    /**
     * @param row
     * @return
     */
    private boolean shouldSkipRow(Item row) {
        if (filterExpr == null) {
            return false;
        }

        // set row properties for given columns
        rowMap.clear();
        int i = 0;
        for (Item cell : row) {
            if (i < columns.length) {
                Column column = columns[i];
                if (column instanceof GivenColumn) {
                    rowMap.put(column.getProperty(), cell.text());
                }
            }
            i++;
        }
        Boolean accept = (Boolean) filterExpr.getValue(carrotContext);
        return !accept;
    }

    private void callBeforeRow() {
        if (beforeRowInvocation != null) {
            callInvocation(beforeRowInvocation);
        }
    }

    private void callBeforeFirstExpectation() {
        if (beforeFirstExpectationInvocation != null) {
            callInvocation(beforeFirstExpectationInvocation);
        }
    }

    private void callAfterRow() {
        if (afterRowInvocation != null) {
            callInvocation(afterRowInvocation);
        }
    }

    private void callInvocation(Invocation message) {
        try {
            Step step = new Step(message);
            step.execute();
            Result result = step.getResult();
            if (result.getStatus() == Result.Status.EXCEPTION) {
                Actions.updateStatistics(statistics, result);
            }
        }
        // CHECKSTYLE:SKIP
        catch (Exception exc) {
            statistics.exception();
        }
    }

    private void scanAnnotations() {
        if (fixture != null) {
            for (Method method : fixture.getTarget().getClass().getMethods()) {
                if (method.isAnnotationPresent(BeforeRow.class) || method.getName().equals("reset")) {
                    beforeRowInvocation = new MethodInvocation(fixture.getTarget(), method);
                }

                if (method.isAnnotationPresent(BeforeFirstExpectation.class)
                        || method.getName().equals("execute")) {
                    beforeFirstExpectationInvocation = new MethodInvocation(fixture.getTarget(),
                            method);
                }

                if (method.isAnnotationPresent(AfterRow.class)) {
                    afterRowInvocation = new MethodInvocation(fixture.getTarget(), method);
                }
            }
        }
    }

    protected void processCell(Column column, Item cell) {
        try {
            statistics.accumulate(column.processCell(cell));
        }
        // CHECKSTYLE:SKIP
        catch (Exception exc) {
            cell.mark(new ExceptionMarker(exc));
            statistics.exception();
        }
    }
}
