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

package org.ops4j.pax.carrot.sample1;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.pax.carrot.annotation.CollectionProvider;


/**
 * @author Harald Wellmann
 *
 */
public class States {
    
    private static String[][] rawStates = {
        { "Baden-Württemberg", "Stuttgart"},
        { "Bayern", "München" },  
        { "Berlin", "Berlin" },  
        { "Brandenburg", "Potsdam" },  
        { "Bremen", "Bremen" },  
        { "Hamburg", "Hamburg" },  
        { "Hessen", "Wiesbaden" },
        { "Mecklenburg-Vorpommern", "Schwerin" },
        { "Niedersachsen", "Hannover" },
        { "Nordrhein-Westfalen", "Düsseldorf"},
        { "Rheinland-Pfalz", "Mainz"},
        { "Saarland", "Saarbrücken"},
        { "Sachsen", "Dresden"},
        { "Sachsen-Anhalt", "Magdeburg"},
        { "Schleswig-Holstein", "Kiel"},
        { "Thüringen", "Erfurt"},
    };
    
    private static List<State> states = new ArrayList<State>();
    
    static {
        for (String[] data : rawStates) {
            states.add(new State(data[0], data[1]));
        }
    }
    
    @CollectionProvider
    public List<State> getStates() {
        return states;
    }
}
