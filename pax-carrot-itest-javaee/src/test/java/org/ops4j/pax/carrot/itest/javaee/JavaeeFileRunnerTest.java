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

package org.ops4j.pax.carrot.itest.javaee;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.carrot.api.ExecutionContextFactory;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.runner.FileRunner;
import org.ops4j.pax.exam.junit.PaxExam;

/**
 * @author Harald Wellmann
 * 
 */
@RunWith(PaxExam.class)
public class JavaeeFileRunnerTest {

    @Inject
    private ExecutionContextFactory ecf;

    private File inputDir;

    private File outputDir;

    @Before
    public void setUp() {
        inputDir = new File("src/test/carrot");
        outputDir = new File("target/carrot");
        outputDir.mkdirs();
    }


    @Test
    public void runCalculatorTest() {
        FileRunner runner = new FileRunner(ecf.newInstance(null), inputDir, outputDir, "calculator/calculator.html");
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(6));
        assertThat(stats.getNumRight(), is(3));
        assertThat(stats.getNumWrong(), is(2));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(1));
    }
}
