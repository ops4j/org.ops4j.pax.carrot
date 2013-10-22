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

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.runner.DefaultExecutionContextFactory;
import org.ops4j.pax.carrot.runner.FileRunner;


public class FileRunnerTest {

    private File inputDir;

    private File outputDir;

    private ExecutionContext context;

    @Before
    public void setUp() {
        context = new DefaultExecutionContextFactory().newInstance(null);
        inputDir = new File("src/test/carrot");
        outputDir = new File("target/carrot");
        outputDir.mkdirs();
    }

    @Test
    public void runCalculatorTest() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir, "calculator/calculator.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(6));
        assertThat(stats.getNumRight(), is(3));
        assertThat(stats.getNumWrong(), is(2));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(1));
    }

    @Test
    public void runCalculatorTestWithFilter() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir,
                "calculator/calculatorFiltered.html");
        runner.setFilter("#{row.a == 2 and row.b == 3}");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(1));
        assertThat(stats.getNumRight(), is(1));
        assertThat(stats.getNumWrong(), is(0));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(0));
    }


    @Test
    public void runStatesTest() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir, "states/states.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(98));
        assertThat(stats.getNumRight(), is(92));
        assertThat(stats.getNumWrong(), is(6));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(0));
    }

    @Test
    public void runGreeterTest() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir, "greeter/greeter.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(1));
        assertThat(stats.getNumRight(), is(1));
        assertThat(stats.getNumWrong(), is(0));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(0));
    }

    @Test
    public void runWhiteSpaceTest() {
        FileRunner runner = new FileRunner(context, inputDir, outputDir, "text/whitespace.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(22));
        assertThat(stats.getNumRight(), is(14));
        assertThat(stats.getNumWrong(), is(8));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(0));
    }
}
