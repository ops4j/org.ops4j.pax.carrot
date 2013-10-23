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

import static org.ops4j.pax.carrot.step.Actions.markException;
import static org.ops4j.pax.carrot.step.Actions.markItem;
import static org.ops4j.pax.carrot.step.Actions.markStopped;
import static org.ops4j.pax.carrot.step.Actions.updateStatistics;

import org.ops4j.pax.carrot.api.CarrotException;
import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.FixtureFactory;
import org.ops4j.pax.carrot.api.Interpreter;
import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.fixture.Fixture;
import org.ops4j.pax.carrot.invocation.Invocation;
import org.ops4j.pax.carrot.marker.ExceptionMarker;
import org.ops4j.pax.carrot.step.Result;
import org.ops4j.pax.carrot.step.Result.Status;
import org.ops4j.pax.carrot.step.Step;

/**
 * An interpreter for working with multiple fixtures and accessing individual properties or methods.
 * 
 * @author Harald Wellmann
 * 
 */
public class ActionInterpreter implements Interpreter {

    protected Statistics statistics = new Statistics();

    private Fixture currentFixture;

    private ExecutionContext carrotContext;

    public ActionInterpreter(ExecutionContext context) {
        this.carrotContext = context;
    }

    public Statistics statistics() {
        return this.statistics;
    }

    protected void processRow(Item row) {
        Item actionCell = row.at(0, 0);
        if (actionCell == null) {
            throw new IllegalStateException("no action cell in current row");
        }

        String action = actionCell.text();
        Item cell = actionCell.nextSibling();
        if (action.equals("start")) {
            processStartRow(cell);
        }
        else if (action.equals("check")) {
            processCheckRow(cell);
        }
        else if (action.equals("press")) {
            processPressRow(cell);
        }
        else if (action.equals("enter")) {
            processEnterRow(cell);
        }
    }

    /**
     * @param row
     */
    private void processStartRow(Item cell) {
        try {
            String fixtureName = cell.text();
            FixtureFactory fixtureFactory = carrotContext.getFixtureFactory();
            currentFixture = fixtureFactory.createFixture(fixtureName);
        }
        catch (CarrotException exc) {
            cell.mark(new ExceptionMarker(exc));
            statistics.exception();
            carrotContext.setStopOnFirstFailure(true);
        }
    }

    /**
     * @param cell
     */
    private void processCheckRow(Item cell) {
        String property = cell.text();
        Invocation invocation = null;
        if (currentFixture.canGet(property)) {
            invocation = currentFixture.deferredGet(property);
        }
        else if (currentFixture.hasSimpleMethod(property)) {
            invocation = currentFixture.deferredSimpleMethod(property);
        }
        else {
            return;
        }
        Item expectedCell = cell.nextSibling();
        if (expectedCell == null) {
            return;
        }
        String expected = expectedCell.text();
        Step step = new Step(invocation);
        step.expect(expected);
        step.execute();
        Result result = step.getResult();
        updateStatistics(statistics, result);
        markItem(expectedCell, result, true);
    }

    /**
     * @param cell
     */
    private void processPressRow(Item cell) {
        String methodName = cell.text();
        if (currentFixture.hasSimpleMethod(methodName)) {
            Step step = new Step(currentFixture.deferredSimpleMethod(methodName));
            step.execute();
            Result result = step.getResult();
            if (result.getStatus() == Status.EXCEPTION) {
                updateStatistics(statistics, result);
                markException(cell, result);
            }
        }
    }

    /**
     * @param row
     */
    private void processEnterRow(Item row) {
        String property = row.text();
        Invocation invocation = null;
        Item argCell = row.nextSibling();
        String value = argCell.text();
        carrotContext.setVariable("arg", value);
        if (currentFixture.canSet(property)) {
            invocation = currentFixture.deferredSet(property);
        }
        else {
            invocation = currentFixture.deferredNonStandardSet(property);
        }
        Step step = new Step(invocation);
        step.execute(value);
        Result result = step.getResult();
        if (result.getException() != null) {
            updateStatistics(statistics, result);
            markException(argCell, result);
        }
    }

    public void interpret(Item table, Statistics stats) {
        this.statistics = stats;
        for (Item row = table.at(0, 1); row != null; row = row.nextSibling()) {
            processRow(row);
            if (carrotContext.shouldStop(stats)) {
                markStopped(row.appendChild());
                break;
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
