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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.ops4j.pax.carrot.runner.listener.RunnerListener;

/**
 * Use this class to run a suite of Pax Carrot tests under JUnit. To do so, create a class with the
 * following annotations:
 * 
 * <pre>
 * &#64;RunWith(CarrotSuite.class)
 * &#64;CarrotConfiguration(MyCarrotSuite.Configuration.class)
 * public class MyCarrotSuite {
 *     
 *     public static Configuration extends DefaultCarrotConfiguration {
 *     
 *         &#64;Override
 *         public String getInputDir() {
 *            return System.getProperty("myCarrotRoot");
 *         }
 *     
 *         &#64;Override
 *         public String getOutputDir() {
 *            return "build/html";
 *         }
 *     }
 * }
 * </pre>
 * 
 * The suite first builds a list of input files to be run as Pax Carrot tests. The input files are
 * located under the {@code inputDir} root. The file patterns to be included or excluded are
 * specified by the {@code includes} and {@code excludes} properties. The default include pattern is
 * <code>**&#47;*.html</code>, the exclude pattern list is empty by default. Each of these
 * properties can be a list of Strings interpreted as Ant file patterns.
 * <p>
 * The suite then runs all files matching at least one of the include patterns and none of the
 * exclude patterns.
 * <p>
 * Each test file will be a direct child of the suite, named by its relative path.
 * 
 * @author hwellmann
 * 
 */
public class CarrotSuite extends Suite {

    private List<Runner> runners = new ArrayList<Runner>();

    private RunnerListener listener;

    /**
     * Constructor for internal use by JUnit. Applications may only use this class as argument to
     * the {@link RunWith} annotation.
     * 
     * @param klass the class to be run as a test suite
     * @throws InitializationError
     */
    public CarrotSuite(Class<?> klass) throws InitializationError {
        super(klass, Collections.<Runner> emptyList());
        buildRunners();
    }

    /**
     * Builds a list of Runners, one for each file matching the input patterns.
     * 
     * @throws InitializationError
     */
    private void buildRunners() throws InitializationError {
        CarrotConfiguration fc = getTestClass().getJavaClass().getAnnotation(
                CarrotConfiguration.class);
        try {
            DefaultCarrotConfiguration config = (fc == null) ? new DefaultCarrotConfiguration()
                    : fc.value().newInstance();

            assert config.getInputDir() != null;
            assert config.getOutputDir() != null;
            assert config.getIncludes() != null;
            assert config.getExcludes() != null;
            assert config.getRunnerListener() != null;

            File inputDirectory = new File(config.getInputDir());
            File outputDirectory = new File(config.getOutputDir());
            listener = config.getRunnerListener();

            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(inputDirectory);
            scanner.setIncludes(config.getIncludes());
            scanner.setExcludes(config.getExcludes());
            scanner.scan();
            String[] files = scanner.getIncludedFiles();
            assert files != null;
            if (files.length == 0) {
                throw new InitializationError("no matching input files");
            }
            // Scanner returns files in random order, so sort them.
            Arrays.sort(files);
            for (String testPath : files) {
                CarrotRunner runner = new CarrotRunner(inputDirectory, outputDirectory, testPath,
                        listener, getTestClass());
                runners.add(runner);
            }

        }
        catch (InstantiationException exc) {
            throw new InitializationError(exc);
        }
        catch (IllegalAccessException exc) {
            throw new InitializationError(exc);
        }

    }

    /**
     * Returns the list of runners corresponding to Pax Carrot test files.
     * 
     * @return Carrot test runners
     * 
     */
    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
        listener.afterSuite();
    }
}
