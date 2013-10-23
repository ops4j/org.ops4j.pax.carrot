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

import org.jsoup.helper.StringUtil;
import org.ops4j.pax.carrot.api.FixtureFactory;
import org.ops4j.pax.carrot.api.Interpreter;
import org.ops4j.pax.carrot.api.InterpreterSelector;
import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.fixture.Fixture;
import org.ops4j.pax.carrot.marker.ExceptionMarker;

public class DefaultInterpreterSelector implements InterpreterSelector {

    private FixtureFactory fixtureFactory;

    public DefaultInterpreterSelector(FixtureFactory fixtureFactory) {
        this.fixtureFactory = fixtureFactory;
    }

    public Interpreter selectInterpreter(Item table) {
        Item cells = table.at(0, 0, 0);
        if (cells == null) {
            return new NoOpInterpreter();
        }

        String interpreterName = cells.at(0).text();
        try {
            String fixtureName = null;
            if (cells.at(0).hasSiblings()) {
                fixtureName = cells.at(1).text();
            }
            return createInterpreter(interpreterName, fixtureName);
        }
        // CHECKSTYLE:SKIP
        catch (Throwable t) {
            cells.at(0).mark(new ExceptionMarker(t));
            return new NoOpInterpreter(new Statistics(0, 0, 1, 0));
        }
    }

    private Interpreter createInterpreter(String interpreterName, String fixtureName) {
        // action interpreter does not require a fixture name in the first row
        if (interpreterName.equals("action")) {
            return new ActionInterpreter(fixtureFactory.getContext());
        }
        
        // all other interpreters do require a fixture name
        if (StringUtil.isBlank(fixtureName)) {
            throw new IllegalArgumentException("fixture name required");
        }
        
        Fixture fixture = fixtureFactory.createFixture(fixtureName);
        if (interpreterName.matches("rule(s?) for")) {
            return new RuleForInterpreter(fixture);
        }
        if (interpreterName.equals("set of")) {
            return new SetOfInterpreter(fixture);
        }
        return new NoOpInterpreter();
    }
}
