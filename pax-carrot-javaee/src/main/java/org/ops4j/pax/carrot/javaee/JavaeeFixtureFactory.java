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

package org.ops4j.pax.carrot.javaee;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.FixtureFactory;
import org.ops4j.pax.carrot.fixture.BeanFixture;
import org.ops4j.pax.carrot.fixture.Fixture;


/**
 * @author Harald Wellmann
 *
 */
@ApplicationScoped
public class JavaeeFixtureFactory implements FixtureFactory {
    
    @Inject
    private BeanManager beanManager;

    private ExecutionContext executionContext;
    

    @Override
    public Fixture createFixture(String fixtureName) {
        Set<Bean<?>> beans = beanManager.getBeans(fixtureName);
        if (beans.isEmpty()) {
            int dot = fixtureName.lastIndexOf('.');
            char first = Character.toLowerCase(fixtureName.charAt(dot + 1));
            String simpleName = first + fixtureName.substring(dot + 2);
            beans = beanManager.getBeans(simpleName);
        }
        Bean<? extends Object> bean = beanManager.resolve(beans);
        CreationalContext<Object> cc = beanManager.createCreationalContext(null);
        Object target = beanManager.getReference(bean, bean.getBeanClass(), cc);
        return new BeanFixture(target, executionContext);
    }

    @Override
    public void setContext(ExecutionContext context) {
        this.executionContext = context;
    }

    @Override
    public ExecutionContext getContext() {
        return executionContext;
    }
}
