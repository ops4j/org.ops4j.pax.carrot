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
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ops4j.pax.carrot.api.CarrotException;
import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.Interpreter;
import org.ops4j.pax.carrot.api.Item;
import org.ops4j.pax.carrot.api.Specification;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.html.DocumentProcessor;
import org.ops4j.pax.carrot.html.HtmlSpecification;
import org.ops4j.pax.carrot.html.fit.FitCompatibilityProcessor;
import org.ops4j.pax.carrot.runner.listener.DefaultRunnerListener;
import org.ops4j.pax.carrot.runner.listener.RunnerListener;


/**
 * Runs a single Carrot test.
 * 
 * @see TreeRunner
 * @author hwellmann
 * 
 */
public class FileRunner {

    /** Input directory for Carrot tests. */
    private File inputDir;

    /** Output directory for Carrot tests. */
    private File outputDir;

    /**
     * Relative path of fit test to be run. The runner runs a file with this path relative to the
     * input directory and writes the processed to file to the same path relative to the output
     * directory.
     */
    private String testPath;

    private Statistics result = new Statistics();

    private RunnerListener listener;

    private String filter;

    private ExecutionContext context;

    public FileRunner(ExecutionContext context, File inputDir, File outputDir, String testPath) {
        this(context, inputDir, outputDir, testPath, new DefaultRunnerListener());
    }

    public FileRunner(ExecutionContext context, File inputDir, File outputDir, String testPath, RunnerListener listener) {
        this.context = context;
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.testPath = testPath;
        this.listener = listener;

    }

    /**
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }


    /**
     * @param filter the filter to set
     */
    public void setFilter(String filter) {
        this.filter = filter;
        context.setVariable("filter", filter);
    }

    public boolean run() {
        File inputFile = new File(inputDir, testPath);
        File outputFile = new File(outputDir, testPath);
        try {
            ensureParentDirExists(outputFile);
            return run(inputFile, outputFile);
        }
        catch (IOException exc) {
            throw new CarrotException(exc);
        }
    }

    private void ensureParentDirExists(File outputFile) throws IOException {
        File parentDir = outputFile.getParentFile();
        if (parentDir.exists()) {
            if (!parentDir.isDirectory()) {
                throw new IOException(parentDir + " is not a directory");
            }
        }
        else if (!parentDir.mkdirs()) {
            throw new IOException("cannot create " + parentDir);
        }
    }

    private boolean run(File in, File out) throws IOException {
        Document document = Jsoup.parse(in, null);
        processDocument(document);
        Specification specification = new HtmlSpecification(document);

        runSpecification(specification);

        FileWriter writer = new FileWriter(out);
        writer.write(document.html());
        writer.close();
        return true;
    }

    private void processDocument(Document document) {
        String compatibility = System.getProperty("pax.carrot.compatibility");
        if (compatibility != null && compatibility.equals("fit")) {
            DocumentProcessor processor = new FitCompatibilityProcessor();
            processor.process(document);
        }
    }

    private void runSpecification(Specification specification) {
        listener.beforeTest(testPath);
        for (Item table : specification) {
            Interpreter interpreter = context.getInterpreterSelector().selectInterpreter(table);
            interpreter.interpret(table, result);
            if (!context.canContinue(result)) {
                break;
            }
        }
        listener.afterTest(result);
    }

    public Statistics getResult() {
        return result;
    }
}
