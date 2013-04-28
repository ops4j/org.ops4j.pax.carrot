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

package org.ops4j.pax.carrot.itest.osgi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.File;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.carrot.api.FixtureFactory;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.runner.FileRunner;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;

/**
 * @author Harald Wellmann
 * 
 */
@RunWith(PaxExam.class)
public class OsgiFileRunnerTest {

    @Inject
    private BundleContext bc;

    @Inject
    private FixtureFactory fixtureFactory;

    private File inputDir;

    private File outputDir;

    @Before
    public void setUp() {
        inputDir = new File("src/test/carrot");
        outputDir = new File("target/carrot");
        outputDir.mkdirs();
    }

    @Configuration
    public static Option[] configuration() {

        return options(
            systemProperty("osgi.console").value("6666"),
            systemProperty("logback.configurationFile")
                .value("file:src/test/resources/logback.xml"),

            // Bundles under test
            workspaceBundle("pax-carrot-core"), workspaceBundle("pax-carrot-html"),
            workspaceBundle("pax-carrot-osgi"), workspaceBundle("pax-carrot-runner"),
            workspaceBundle("pax-carrot-samples/pax-carrot-sample1"),

            mavenBundle("de.odysseus.juel", "juel-impl").versionAsInProject(),
            mavenBundle("org.jsoup", "jsoup").versionAsInProject(),
            mavenBundle("org.apache.geronimo.specs", "geronimo-el_2.2_spec").versionAsInProject(),
            mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.2"),
            wrappedBundle(mavenBundle("org.codehaus.plexus", "plexus-utils").versionAsInProject())
                .exports("*"),

            // Logging
            mavenBundle("org.slf4j", "slf4j-api", "1.6.4"),
            mavenBundle("ch.qos.logback", "logback-core", "1.0.6"),
            mavenBundle("ch.qos.logback", "logback-classic", "1.0.6"),

            junitBundles());
    }

    public static UrlProvisionOption workspaceBundle(String pathFromRoot) {
        String url = String.format("reference:file:%s/../%s/target/classes",
            PathUtils.getBaseDir(), pathFromRoot);
        return bundle(url);
    }

    @Test
    public void runCalculatorTest() {
        assertThat(bc, is(notNullValue()));
        assertThat(fixtureFactory, is(notNullValue()));

        FileRunner runner = new FileRunner(inputDir, outputDir, "calculator/calculator.html",
            fixtureFactory);
        runner.run();
        Statistics stats = runner.getResult();
        assertThat(stats.totalCount(), is(6));
        assertThat(stats.getNumRight(), is(3));
        assertThat(stats.getNumWrong(), is(2));
        assertThat(stats.getNumIgnored(), is(0));
        assertThat(stats.getNumException(), is(1));
    }
}
