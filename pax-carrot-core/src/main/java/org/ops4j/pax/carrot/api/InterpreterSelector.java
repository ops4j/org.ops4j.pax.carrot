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

package org.ops4j.pax.carrot.api;


/**
 * Selects an interpreter for the given table. The selection is determined by the contents
 * of the first table cell.
 * 
 * @author Harald Wellmann
 *
 */
public interface InterpreterSelector {

    /**
     * Selects an interpreter for the given table.
     * @param table a specification table
     * @return interpreter for this table
     */
    Interpreter selectInterpreter(Item table);
}
