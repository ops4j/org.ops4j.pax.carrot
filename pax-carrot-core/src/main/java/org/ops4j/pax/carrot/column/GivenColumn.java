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

import static org.ops4j.pax.carrot.step.Actions.markItem;
import static org.ops4j.pax.carrot.step.Actions.updateStatistics;

import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.invocation.Invocation;
import org.ops4j.pax.carrot.step.Step;
import org.ops4j.pax.carrot.step.Result;

/**
 * A given column, containing a value to be set on a fixture property.
 * 
 * @author Harald Wellmann
 *
 */
public class GivenColumn extends Column {

    private Invocation invocation;

    public GivenColumn(Invocation invocation, String property) {
        super(property);
        this.invocation = invocation;
    }

    public Statistics processCell(Item cell) {
        Statistics stats = new Statistics();

        Step step = new Step(invocation);
        step.execute(cell.text());
        Result result = step.getResult();
        if (result.getStatus() == Result.Status.EXCEPTION) {
            markItem(cell, result, true);
            updateStatistics(stats, result);
        }
        return stats;
    }
}
