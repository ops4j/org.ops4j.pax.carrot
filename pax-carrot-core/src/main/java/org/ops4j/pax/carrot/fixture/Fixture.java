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

package org.ops4j.pax.carrot.fixture;

import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.invocation.Invocation;

/**
 * Represents a fixture instance. Users writing fixture classes <em>shall not</em> implement this
 * interface.
 * <p>
 * Fixture classes do not depend on any Pax Carrot APIs except annotations.
 * <p>
 * Each fixture class instantiated by the system under test or the Pax Carrot runtime environment is
 * proxied by a wrapper implementing this interface.
 * <p>
 * A Fixture property is either a Java Bean property with accessor methods following the usual
 * naming conventions, or a public field of the given name.
 * <p>
 * A simple method is a zero-argument method which may be invoked by an {@code enter} row of an action table.
 * <p>
 * A non-standard setter is a method with a setter signature but a name that does not follow Java bean
 * conventions, e.g. {@code numItems(int)} instead of {@code setNumItems(int)}.
 * <p>
 * Public fields and non-standard setters are supported only for easier migration from Fit to Pax Carrot.
 * To use these deprecated features, the system property {@code pax.carrot.compatibility} must be set
 * to {@code fit}.
 * 
 * @author Harald Wellmann
 */
public interface Fixture {

    /** Can we set the given property on this fixture? */
    boolean canSet(String property);

    /** Can we get the given property from this fixture? */
    boolean canGet(String property);

    /** Does this fixture have a simple (i.e. zero argument) method with the given name? */
    boolean hasSimpleMethod(String methodName);
    
    /**
     * Returns an invocation object for setting the given property.
     * 
     * @param property
     * @return
     */
    Invocation deferredGet(String property);

    /**
     * Returns an invocation object for getting the given property.
     * 
     * @param property
     * @return
     */
    Invocation deferredSet(String property);

    /**
     * Returns an invocation object for the given simple method.
     * 
     * @param methodName name of zero-argument method
     * @return
     */
    Invocation deferredSimpleMethod(String methodName);

    /**
     * Returns an invocation object for the given non-standard setter method.
     * 
     * @param methodName name of one-argument method
     * @return
     */
    Invocation deferredNonStandardSet(String methodName);

    /**
     * Returns the fixture instance proxied by this wrapper.
     * 
     * @return
     */
    Object getTarget();

    /**
     * Returns the execution context for this fixture.
     * 
     * @return
     */
    ExecutionContext getContext();

    /**
     * Wraps dependent target object in a fixture of the same type as the current one, using the
     * given name for the dependent object.
     * 
     * @param target
     *            an object depending on the target of the current fixture
     * @param name
     *            name to be used for the wrapped dependent target
     */
    Fixture wrap(Object target, String name);
}
