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

import org.ops4j.pax.carrot.api.CarrotException;
import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.FixtureFactory;

public class ClassPathFixtureFactory implements FixtureFactory {

    private ExecutionContext context;
    
    
    @Override
    public Fixture createFixture(String fixtureName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> klass = cl.loadClass(fixtureName);
            Object target = klass.newInstance();
            return new BeanFixture(target, context);
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
}
