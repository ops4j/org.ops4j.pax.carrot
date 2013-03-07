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

package org.ops4j.pax.carrot.runner.report;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.ops4j.pax.carrot.api.CarrotException;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.runner.listener.RunnerListener;
import org.ops4j.pax.carrot.runner.report.jaxb.Summary;
import org.ops4j.pax.carrot.runner.report.jaxb.TestResult;

public class ReportGenerator implements RunnerListener {
    
    private Summary summary;
    private File outputDir;
    private Statistics totalStatistics;
    private String testPath;

    public ReportGenerator(File inputDir, File outputDir) {        
        this.summary = new Summary();
        this.totalStatistics = new Statistics();
        this.outputDir = outputDir;
        summary.setInputDir(inputDir.getPath());
        summary.setOutputDir(outputDir.getPath());
    }

    @Override
    public void beforeTest(String path) {
        this.testPath = path;
    }

    @Override
    public void afterTest(Statistics result) {
        totalStatistics.accumulate(result);
        TestResult testResult = toTestResult(result);
        testResult.setPath(testPath);
        summary.getTest().add(testResult);
    }

    /**
     * @param result
     * @return
     */
    private TestResult toTestResult(Statistics stats) {
        TestResult result = new TestResult();
        boolean success = (stats.getNumException() == 0) && (stats.getNumWrong() == 0);
        result.setPassed(success);
        result.setRight(stats.getNumRight());
        result.setWrong(stats.getNumWrong());
        result.setIgnored(stats.getNumIgnored());
        result.setExceptions(stats.getNumException());

        return result;
    }

    public void createReports() {
        buildSummary();
        printSummary();
    }
    
    public Summary getSummary() {
        return summary;
    }
    
    private void buildSummary() {
        summary.setNumTests(summary.getTest().size());
        
        boolean success = !totalStatistics.hasFailed();
        summary.setPassed(success);
        summary.setRight(totalStatistics.getNumRight());
        summary.setWrong(totalStatistics.getNumWrong());
        summary.setIgnored(totalStatistics.getNumIgnored());
        summary.setExceptions(totalStatistics.getNumException());
    }
    
    private void printSummary() {
        ReportIO writer = new ReportIO(summary);
        File xmlReport = new File(outputDir, ReportIO.CARROT_REPORT_XML);        
        File htmlReport = new File(outputDir, ReportIO.CARROT_REPORT_HTML);        
        File css = new File(outputDir, ReportIO.CARROT_CSS);        
        try {
            writer.writeXml(xmlReport);
            writer.writeHtml(htmlReport);
            writer.writeCss(css);
        }
        catch (JAXBException exc) {
            throw new CarrotException(exc);
        }              
        catch (IOException exc) {
            throw new CarrotException(exc);
        }
    }

    @Override
    public void afterSuite() {
        createReports();
    }
}
