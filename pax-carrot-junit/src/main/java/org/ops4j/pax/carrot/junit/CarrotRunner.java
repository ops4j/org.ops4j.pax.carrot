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

package org.ops4j.pax.carrot.junit;

import java.io.File;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.runner.FileRunner;
import org.ops4j.pax.carrot.runner.listener.RunnerListener;

/**
 * Runs a single Pax Carrot test under JUnit. Do not directly use this class in applications.
 * 
 * @see CarrotSuite
 * @author hwellmann
 *
 */
class CarrotRunner extends Runner implements RunnerListener {
   
    /**
     * Description of this Carrot test for JUnit notifiers. Corresponds to {@code testPath}.
     */
    private Description description;
    
    private FileRunner fileRunner;

    private RunNotifier notifier;

    private RunnerListener listener;
    
    /**
     * Runs a single Carrot test and notifies a listener of the results.
     * @param inputDir  input directory
     * @param outputDir output directory
     * @param testPath  relative path of test (or test output) under 
     *                  inputDir (or outputDir), respectively
     * @param listener  listener to be notified of the results.
     */
    public CarrotRunner(File inputDir, File outputDir, String testPath, RunnerListener listener) {
        this.description = Description.createSuiteDescription(testPath);
        this.fileRunner = new FileRunner(inputDir, outputDir, testPath, this);
        this.listener = listener;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(RunNotifier _notifier) {
        _notifier.fireTestStarted(getDescription());
        try {
            this.notifier = _notifier;
            fileRunner.run();
        }
        // CHECKSTYLE:SKIP
        catch (Exception exc) {
            Failure failure = new Failure(getDescription(), exc);
            _notifier.fireTestFailure(failure);
        }
        _notifier.fireTestFinished(getDescription());
    }

    /**
     * Returns an AssertionError for failed tests and an Exception for tests with unexpected
     * exceptions, to match JUnit conventions.
     * @return Throwable
     */
    private Throwable getThrowable(Statistics stats) {
        String msg = stats.toString();
        if (stats.getNumException() > 0) {
            return new Exception(msg);
        }
        else {
            return new AssertionError(msg);
        }
    }

    @Override
    public void beforeTest(String testPath) {
        listener.beforeTest(testPath);
    }

    @Override
    public void afterTest(Statistics result) {
        listener.afterTest(result);
        if (result.hasFailed()) {
            /*
             * Distinguish failed tests from tests in error by including the appropriate
             * type of Throwable in the Failure.
             */
            Failure failure = new Failure(getDescription(), getThrowable(result));
            notifier.fireTestFailure(failure);
        }
    }

    @Override
    public void afterSuite() {
        listener.afterSuite();
    }
}
