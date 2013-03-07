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

package org.ops4j.pax.carrot.invocation;

/**
 * Represents an interaction with a fixture instance, accessing a property or invoking a
 * method with the given list of arguments.
 * <p>
 * A given invocation has a specific target instance which cannot be changed during the
 * lifetime of this invocation.
 * 
 * @author Harald Wellmann
 *
 */
public interface Invocation {

    /** Executes this invocation on the target fixture with the given argument. */
    Object invoke(Object... args);
}
