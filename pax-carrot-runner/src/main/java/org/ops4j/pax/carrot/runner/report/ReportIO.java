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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.codehaus.plexus.util.IOUtil;
import org.ops4j.pax.carrot.runner.report.jaxb.ObjectFactory;
import org.ops4j.pax.carrot.runner.report.jaxb.Summary;
import org.ops4j.pax.carrot.runner.report.jaxb.TestResult;

public class ReportIO {

    public static final String CARROT_REPORT_XML = "carrot-report.xml";
    public static final String CARROT_REPORT_HTML = "index.html";
    public static final String CARROT_CSS = "carrot.css";

    private Summary summary;
    private PrintWriter writer;

    public ReportIO() {
    }

    public ReportIO(Summary summary) {
        this.summary = summary;
    }

    public void writeCss(File css) throws IOException {
        OutputStream os = new FileOutputStream(css);
        InputStream is = getClass().getResourceAsStream("/css/" + CARROT_CSS);
        IOUtil.copy(is, os);
        is.close();
        os.close();
    }

    public void writeXml(File report) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(Summary.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ObjectFactory factory = new ObjectFactory();
        marshaller.marshal(factory.createSummary(summary), report);
    }

    public void writeHtml(File report) throws IOException {
        writer = new PrintWriter(report, "UTF-8");
        writer.println("<html><head><title>Pax Carrot Test Summary</title>");
        writer.println("<link rel='stylesheet' type='text/css' href='" + CARROT_CSS + "'>");
        writer.println("</head><body>");
        writer.println("<table class='summary'>");
        writeTableHeaderRow();
        writeTotals();
        for (TestResult testResult : summary.getTest()) {
            writeTableRow(testResult);
        }
        writer.println("</table>");
        writer.println("</body></html>");
        writer.close();
    }

    private void writeTotals() {
        writer.println("<tr>");
        writeTableCell("total", "Total (" + summary.getNumTests() + " tests)");
        writeTableCell("right", summary.getRight());
        writeTableCell("wrong", summary.getWrong());
        writeTableCell("ignored", summary.getIgnored());
        writeTableCell("exceptions", summary.getExceptions());
        writeTableCell("sum", getSummarySum());
        writer.println("</tr>");
    }

    private int getSummarySum() {
        return summary.getRight() + summary.getWrong() + summary.getIgnored()
            + summary.getExceptions();
    }

    private void writeTableHeaderRow() {
        writer.println("<tr>");
        writeTableHeaderCell("Test");
        writeTableHeaderCell("Right");
        writeTableHeaderCell("Wrong");
        writeTableHeaderCell("Ignored");
        writeTableHeaderCell("Exceptions");
        writeTableHeaderCell("Sum");
        writer.println("</tr>");
    }

    private void writeTableHeaderCell(String text) {
        writer.println("<th>" + text + "</th>");
    }

    private void writeTableRow(TestResult testResult) {
        writer.println("<tr>");
        writeLinkedTableCell(testResult.getPath());
        writeTableCell("right", testResult.getRight());
        writeTableCell("wrong", testResult.getWrong());
        writeTableCell("ignored", testResult.getIgnored());
        writeTableCell("exceptions", testResult.getExceptions());
        writeTableCell("sum", getSum(testResult));
        writer.println("</tr>");
    }

    private int getSum(TestResult testResult) {
        return testResult.getRight() + testResult.getWrong() + testResult.getIgnored()
            + testResult.getExceptions();
    }

    private void writeTableCell(String style, String text) {
        writer.println("<td class='" + style + "'>" + text + "</td>");
    }

    private void writeLinkedTableCell(String text) {
        writer.println("<td><a href='" + text + "'>" + text + "</a></td>");
    }

    private void writeTableCell(String style, int count) {
        String actualStyle = (count == 0) ? "none" : style;
        writer.println("<td class='" + actualStyle + "'>" + count + "</td>");
    }

    @SuppressWarnings("unchecked")
    public Summary readXml(InputStream is) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        JAXBElement<Summary> summaryRoot = (JAXBElement<Summary>) unmarshaller.unmarshal(is);
        return summaryRoot.getValue();
    }

    public static String format(TestResult result) {
        return String.format("%d right, %d wrong, %d ignored, %d exceptions", result.getRight(),
            result.getWrong(), result.getIgnored(), result.getExceptions());
    }

}
