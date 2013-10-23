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
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

/**
 * A fixture loader which produces Spring beans or, failing that, unmanaged fixtures with injection
 * points satisfied from a given Spring application context.
 * <p>
 * The {@link TestContextManager} is used to obtain an application context, based on a
 * {@link ContextConfiguration} annotation, either on a fixture class or on a test suite class.
 * <p>
 * At first, if the configuration object of the {@link ExecutionContext} is of type {@code Class}, and if this
 * class has a {@code ContextConfiguration} annotation, then the factory creates a test context for this
 * class and uses the corresponding application context to look up a fixture bean by name. On success,
 * this bean is returned to the caller.
 * <p>
 * Otherwise, the given fixture name is interpreted
 * as a class name as a fall-back. The factory then loads the class via the thread context 
 * class loader and creates an instance. 
 * <p>
 * If the fixture class has a {@link ContextConfiguration} annotation, then the factory creates a test context for this
 * klass and prepares the corresponding test instance, which includes dependency injection from the application context. 
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
     * @param fixtureClassName name of fixture class
     */
    @Override
    public Fixture createFixture(String fixtureName) {
        try {
            Object target = lookupBeanByName(fixtureName);
            if (target == null) {
                target = createTarget(fixtureName);
                Class<?> klass = target.getClass();
                ContextConfiguration cc = klass.getAnnotation(ContextConfiguration.class);
                if (cc != null) {
                    TestContextManager contextManager = new TestContextManager(klass);
                    contextManager.prepareTestInstance(target);
                }
            }
            BeanFixture fixture = new BeanFixture(target, context);
            return fixture;
        }
        // CHECKSTYLE:SKIP : Spring API
        catch (Exception exc) {
            throw new CarrotException("dependency injection failed for " + fixtureName, exc);
        }
    }

    private Object lookupBeanByName(String fixtureName) throws Exception {

        if (context.getConfiguration() instanceof Class<?>) {
            Class<?> configClass = (Class<?>) context.getConfiguration();
            ContextConfiguration cc = configClass.getAnnotation(ContextConfiguration.class);
            if (cc != null) {
                TestContextManager contextManager = new TestContextManager(configClass);
                ApplicationContextHolder applicationContextHolder = new ApplicationContextHolder();
                contextManager.registerTestExecutionListeners(applicationContextHolder);
                contextManager.prepareTestInstance(configClass.newInstance());
                ApplicationContext applicationContext = applicationContextHolder
                        .getApplicationContext();
                if (applicationContext.containsBean(fixtureName)) {
                    return applicationContext.getBean(fixtureName);
                }
            }
        }
        return null;
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
     * @param context the context to set
     */
    public void setContext(ExecutionContext context) {
        this.context = context;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
    }
}
