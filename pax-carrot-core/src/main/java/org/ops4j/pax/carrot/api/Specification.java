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
 * A Specification contains one or more tables to be interpreted by an interpreter defined by the
 * first cell or the first row of the table.
 * <p>
 * Each {@link Item} returned by the iterator of this corresponds to a specification table.
 * 
 * @author Harald Wellmann
 * 
 */
public interface Specification extends Iterable<Item> {

}
