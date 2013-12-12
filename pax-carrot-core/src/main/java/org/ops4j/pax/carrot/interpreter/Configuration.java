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

package org.ops4j.pax.carrot.interpreter;

/**
 * Configuration of Pax Carrot runtime.
 * 
 * @author Harald Wellmann
 * @version $Rev: 53870 $ $Date: 2013-02-12 11:32:44 +0100 (Di, 12. Feb 2013) $
 * @since 12.12.2013
 */
public class Configuration {
    
    private Configuration() {
    }

    /**
     * Are we using strict mode?
     * @return false for Fit compatibility, true otherwise.
     */
    public static boolean isStrict() {
        String compat = System.getProperty("pax.carrot.compatibility");
        if (compat == null) {
            return true;
        }
        return !compat.equals("fit");
    }

    /**
     * Are we in Fit compatibility mode?
     * @return true for Fit compatibility, false otherwise.
     */
    public static boolean isFitCompatible() {
        String compat = System.getProperty("pax.carrot.compatibility");
        if (compat == null) {
            return false;
        }
        return compat.equals("fit");
    }
}
