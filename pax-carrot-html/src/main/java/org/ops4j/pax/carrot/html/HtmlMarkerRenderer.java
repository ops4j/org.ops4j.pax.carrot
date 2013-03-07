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

package org.ops4j.pax.carrot.html;

import static org.ops4j.pax.carrot.html.Colors.BACKGROUND_COLOR;
import static org.ops4j.pax.carrot.html.Colors.GREEN;
import static org.ops4j.pax.carrot.html.Colors.GREY;
import static org.ops4j.pax.carrot.html.Colors.RED;
import static org.ops4j.pax.carrot.html.Colors.YELLOW;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jsoup.nodes.Element;
import org.ops4j.pax.carrot.api.Marker;
import org.ops4j.pax.carrot.marker.ExceptionMarker;
import org.ops4j.pax.carrot.marker.IgnoredMarker;
import org.ops4j.pax.carrot.marker.MissingMarker;
import org.ops4j.pax.carrot.marker.RightMarker;
import org.ops4j.pax.carrot.marker.StoppedMarker;
import org.ops4j.pax.carrot.marker.SurplusMarker;
import org.ops4j.pax.carrot.marker.WrongMarker;


public class HtmlMarkerRenderer {
    public void render(Marker marker, Element elem) {
        if (marker instanceof RightMarker) {
            renderRightMarker((RightMarker) marker, elem);
        }
        else if (marker instanceof WrongMarker) {
            renderWrongMarker((WrongMarker) marker, elem);
        }
        else if (marker instanceof IgnoredMarker) {
            renderIgnoredMarker((IgnoredMarker) marker, elem);
        }
        else if (marker instanceof ExceptionMarker) {
            renderExceptionMarker((ExceptionMarker) marker, elem);
        }
        else if (marker instanceof StoppedMarker) {
            renderStoppedMarker((StoppedMarker) marker, elem);
        }
        else if (marker instanceof MissingMarker) {
            renderMissingMarker((MissingMarker) marker, elem);
        }
        else if (marker instanceof SurplusMarker) {
            renderSurplusMarker((SurplusMarker) marker, elem);
        }
    }

    private void renderStoppedMarker(StoppedMarker marker, Element elem) {
        elem.attr("style", String.format("%s: %s;", BACKGROUND_COLOR, RED));
        elem.html("<em>Stopped</em>");
    }

    private void renderExceptionMarker(ExceptionMarker marker, Element elem) {
        elem.attr("style", String.format("%s: %s;", BACKGROUND_COLOR, YELLOW));
        StringWriter stackTrace = new StringWriter();
        PrintWriter writer = new PrintWriter(stackTrace);
        marker.getException().printStackTrace(writer);
        elem.html(String.format("%s<hr/><pre><font size='-2'>%s</font></pre>", elem.text(),
                stackTrace));
    }

    private void renderIgnoredMarker(IgnoredMarker marker, Element elem) {
        elem.attr("style", String.format("%s: %s;", BACKGROUND_COLOR, GREY));
        elem.text(marker.getActual().toString());
    }

    private void renderWrongMarker(WrongMarker marker, Element elem) {
        elem.attr("style", String.format("%s: %s;", BACKGROUND_COLOR, RED));
        String expected = elem.text();
        Object actual = marker.getActual();
        elem.html(String.format("<em>Expected:</em> %s<br/><em>Actual: %s</em>", expected, actual));
    }

    private void renderMissingMarker(MissingMarker marker, Element elem) {
        elem.attr("style", String.format("%s: %s;", BACKGROUND_COLOR, RED));
        String expected = elem.text();
        elem.html(String.format("<em>Missing:</em> %s", expected));
    }

    private void renderSurplusMarker(SurplusMarker marker, Element elem) {
        elem.attr("style", String.format("%s: %s;", BACKGROUND_COLOR, RED));
        String actual = elem.text();
        elem.html(String.format("<em>Surplus:</em> %s", actual));
    }

    private void renderRightMarker(RightMarker marker, Element elem) {
        elem.attr("style", String.format("%s: %s;", BACKGROUND_COLOR, GREEN));
    }
}
