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

package org.ops4j.pax.carrot.html.fit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.ops4j.pax.carrot.api.CarrotException;
import org.ops4j.pax.carrot.html.DocumentProcessor;

public class FitCompatibilityProcessor implements DocumentProcessor {

    private Properties fixtureMap;

    @Override
    public void process(Document document) {
        loadFixtureMap();
        for (Element elem : document.select("table")) {
            processTable(elem);
        }
    }

    private void loadFixtureMap() {
        fixtureMap = new Properties();
        InputStream is = getClass().getResourceAsStream("/fixture.properties");
        if (is == null) {
            return;
        }
        try {
            fixtureMap.load(is);
        }
        catch (IOException exc) {
            throw new CarrotException(exc);
        }
    }

    private void processTable(Element elem) {
        Element fixtureCell = elem.select("tr td").first();
        String fixtureName = fixtureCell.text();
        String interpreter = fixtureMap.getProperty(fixtureName);
        if (interpreter != null) {
            Element actionCell = fixtureCell.parent().prependElement("td");
            actionCell.text(interpreter);
            String colspan = fixtureCell.attr("colspan");
            if (colspan != null) {
                try {
                    int numColumns = Integer.parseInt(colspan) - 1;
                    fixtureCell.attr("colspan", Integer.toString(numColumns));
                }
                catch (NumberFormatException exc) {
                    // ignore
                }
            }
        }
    }
}
