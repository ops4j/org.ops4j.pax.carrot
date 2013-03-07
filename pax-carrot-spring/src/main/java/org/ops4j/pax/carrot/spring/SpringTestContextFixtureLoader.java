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

package org.ops4j.pax.carrot.spring;

import org.ops4j.pax.carrot.api.CarrotException;
import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.FixtureFactory;
import org.ops4j.pax.carrot.fixture.BeanFixture;
import org.ops4j.pax.carrot.fixture.Fixture;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

/**
 * A fixture loader which performs Spring dependency injection on all fixtures annotated with
 * {@link ContextConfiguration}, using a Spring Test Context.
 * 
 * For classes without a {@link ContextConfiguration} annotation, this loader defaults to the
 * standard {@link FixtureLoader} behaviour.
 * 
 * @author Harald Wellmann
 * 
 */
public class SpringTestContextFixtureLoader implements FixtureFactory {

    private ExecutionContext context;

    /**
     * Creates an instance of a fixture class and performs Spring dependency injection if class is
     * annotated with {@link ContextConfiguration}.
     * 
     * @param fixtureClassName
     *            name of fixture class
     */
    @Override
    public Fixture createFixture(String fixtureName) {
        Object target = createTarget(fixtureName);
        Class<?> klass = target.getClass();
        try {
            ContextConfiguration cc = klass.getAnnotation(ContextConfiguration.class);
            if (cc != null) {
                TestContextManager contextManager = new TestContextManager(klass);
                contextManager.prepareTestInstance(target);
            }
            BeanFixture fixture = new BeanFixture(target, context);
            return fixture;
        }
        // CHECKSTYLE:SKIP : Spring API
        catch (Exception exc) {
            throw new CarrotException("dependency injection failed for " + klass.getName(), exc);
        }
    }

    private Object createTarget(String fixtureName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> klass = cl.loadClass(fixtureName);
            Object target = klass.newInstance();
            return target;
        }
        catch (ClassNotFoundException exc) {
            throw new CarrotException(exc);
        }
        catch (InstantiationException exc) {
            throw new CarrotException(exc);
        }
        catch (IllegalAccessException exc) {
            throw new CarrotException(exc);
        }
    }

    /**
     * @param context
     *            the context to set
     */
    public void setContext(ExecutionContext context) {
        this.context = context;
    }
}
