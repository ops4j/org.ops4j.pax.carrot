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

package org.ops4j.pax.carrot.runner;

import java.io.File;
import java.util.Arrays;

import org.codehaus.plexus.util.DirectoryScanner;
import org.ops4j.pax.carrot.api.CarrotException;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.runner.listener.DefaultRunnerListener;
import org.ops4j.pax.carrot.runner.listener.RunnerListener;


public class TreeRunner {

    public static final String DEFAULT_INCLUDE = "**/*.html";

    private File inputDir;

    private File outputDir;

    private String[] includes;

    private String[] excludes;

    private RunnerListener listener;

    private boolean success;

    public TreeRunner(File inputDir, File outputDir) {
        this(inputDir, outputDir, DEFAULT_INCLUDE);
    }

    public TreeRunner(File inputDir, File outputDir, String[] includes) {
        this(inputDir, outputDir, includes, (String[]) null);
    }

    public TreeRunner(File inputDir, File outputDir, String include) {
        this(inputDir, outputDir, new String[] { include }, (String[]) null);
    }

    public TreeRunner(File inputDir, File outputDir, RunnerListener listener) {
        this(inputDir, outputDir, DEFAULT_INCLUDE, listener);
    }

    public TreeRunner(File inputDir, File outputDir, String[] includes, RunnerListener listener) {
        this(inputDir, outputDir, includes, null, listener);
    }

    public TreeRunner(File inputDir, File outputDir, String include, RunnerListener listener) {
        this(inputDir, outputDir, new String[] { include }, null, listener);
    }

    public TreeRunner(File inputDir, File outputDir, String[] includes, String[] excludes) {
        this(inputDir, outputDir, includes, excludes, new DefaultRunnerListener());
    }

    public TreeRunner(File inputDir, File outputDir, String[] includes, String[] excludes,
            RunnerListener listener) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.includes = includes;
        this.excludes = excludes;
        this.listener = listener;
    }

    public boolean run() {
        assert inputDir != null;
        assert outputDir != null;
        assert includes != null;

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(inputDir);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        assert files != null;
        if (files.length == 0) {
            throw new CarrotException("no matching input files");
        }
        // Scanner returns files in random order, so sort them.
        Arrays.sort(files);
        for (String testPath : files) {
            runSingleTest(testPath);
        }
        listener.afterSuite();
        return success;
    }

    private void runSingleTest(String testPath) {
        listener.beforeTest(testPath);
        FileRunner runner = new FileRunner(inputDir, outputDir, testPath);
        boolean passed = runner.run();
        success &= passed;

        Statistics result = runner.getResult();
        listener.afterTest(result);
    }
}
