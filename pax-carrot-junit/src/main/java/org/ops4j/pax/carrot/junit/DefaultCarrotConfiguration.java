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

import org.ops4j.pax.carrot.runner.listener.RunnerListener;
import org.ops4j.pax.carrot.runner.report.ReportGenerator;

/**
 * Default configuration for {@link CarrotSuite}s. For a custom configuration, extend this class and
 * include the derived class in a {@link CarrotConfiguration} on your suite class.
 * 
 * @author Harald Wellmann
 */
public class DefaultCarrotConfiguration {

    public static final String INPUT_DIR = "src/test/carrot";
    public static final String OUTPUT_DIR = "target/carrot";
    public static final String INCLUDE_HTML = "**/*.html";

    public String getInputDir() {
        return INPUT_DIR;
    }

    public String getOutputDir() {
        return OUTPUT_DIR;
    }

    public String[] getIncludes() {
        return new String[] { INCLUDE_HTML };
    }

    public String[] getExcludes() {
        return new String[0];
    }
    
    public RunnerListener getRunnerListener() {
        return new ReportGenerator(new File(getInputDir()), new File(getOutputDir()));
    }
}
