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

package org.ops4j.pax.carrot.step;

import org.ops4j.pax.carrot.api.Markable;
import org.ops4j.pax.carrot.api.Statistics;
import org.ops4j.pax.carrot.marker.ExceptionMarker;
import org.ops4j.pax.carrot.marker.IgnoredMarker;
import org.ops4j.pax.carrot.marker.RightMarker;
import org.ops4j.pax.carrot.marker.StoppedMarker;
import org.ops4j.pax.carrot.marker.WrongMarker;

/**
 * A utility class collecting actions to be executed after a test {@link Step} has produced
 * a {@link Result}.
 * 
 * @author Harald Wellmann
 *
 */
public class Actions {
    
    
    private Actions() {
    }
    
    public static void markItem(Markable markable, Result result, boolean detailed) {
        switch (result.getStatus()) {
            case RIGHT:
                markRight(markable, result);
                return;
            case WRONG:
                markWrong(markable, result, detailed);
                return;
            case EXCEPTION:
                markException(markable, result);
                return;
            case IGNORED:
                markIgnored(markable, result);
                return;
            default:
                throw new IllegalStateException();
        }        
    }

    public static void markRight(Markable markable, Result result) {
        markable.mark(new RightMarker());        
    }
    
    public static void markWrong(Markable markable, Result result, boolean detailed) {
        WrongMarker wrong = new WrongMarker();
        if (detailed) {
            wrong.addDetails(result.getActual());
        }
        markable.mark(wrong);        
    }
    
    public static void markException(Markable markable, Result result) {
        markable.mark(new ExceptionMarker(result.getException()));        
    }
    
    public static void markIgnored(Markable markable, Result result) {
        markable.mark(new IgnoredMarker(result.getActual()));        
    }
    
    public static void markStopped(Markable markable) {
        markable.mark(new StoppedMarker());        
    }
    
    public static void updateStatistics(Statistics stats, Result result) {
        switch (result.getStatus()) {
            case RIGHT:
                stats.right();
                return;
            case WRONG:
                stats.wrong();
                return;
            case IGNORED:
                stats.ignored();
                return;
            case EXCEPTION:
                stats.exception();
                return;
            default:
                throw new IllegalStateException();
        }
    }    
}
