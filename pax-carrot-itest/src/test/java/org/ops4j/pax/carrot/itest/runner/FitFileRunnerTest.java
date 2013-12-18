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

package org.ops4j.pax.carrot.itest.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.runner.FileRunner;


public class FitFileRunnerTest extends FileRunnerTest {

    @Before
    public void enableFit() {
        System.setProperty("pax.carrot.compatibility", "fit");
    }
    
    @After 
    public void disableFit() {
        System.clearProperty("pax.carrot.compatibility");        
    }

    @Test
    public void runFitCalculatorTest() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir, "calculator/fitCalculator.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(6));
        assertThat(stats.getNumRight(), is(3));
        assertThat(stats.getNumWrong(), is(2));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(1));
    }

    @Test
    public void runCalculatorTestWithInvalidColumns() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir, "calculator/calculatorInvalidColumns.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(4));
        assertThat(stats.getNumRight(), is(0));
        assertThat(stats.getNumWrong(), is(4));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(0));
    }
    
    @Test
    public void runGreeterTestWithInvalidActions() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir, "greeter/greeterInvalidActions.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(3));
        assertThat(stats.getNumRight(), is(0));
        assertThat(stats.getNumWrong(), is(1));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(2));
    }
    
    @Test
    public void runStatesTestWithParentheses() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir, "states/statesParentheses.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(98));
        assertThat(stats.getNumRight(), is(92));
        assertThat(stats.getNumWrong(), is(6));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(0));
    }

    
}
