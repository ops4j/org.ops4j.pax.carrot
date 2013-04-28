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

package org.ops4j.pax.carrot.osgi;

import javax.el.ExpressionFactory;

import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.ExecutionContextFactory;
import org.ops4j.pax.carrot.api.FixtureFactory;
import org.ops4j.pax.carrot.api.InterpreterSelector;
import org.ops4j.pax.carrot.el.ELExecutionContext;
import org.ops4j.pax.carrot.interpreter.DefaultInterpreterSelector;


/**
 * @author Harald Wellmann
 *
 */
public class OsgiExecutionContextFactory implements ExecutionContextFactory {
    
    private FixtureFactory fixtureFactory;
    
    private ExpressionFactory expressionFactory;

    @Override
    public ExecutionContext newInstance() {
        InterpreterSelector interpreterSelector = new DefaultInterpreterSelector(fixtureFactory);
        ELExecutionContext context = new ELExecutionContext(expressionFactory, fixtureFactory, interpreterSelector);
        fixtureFactory.setContext(context);

        return context;
    }

    
    /**
     * @return the fixtureFactory
     */
    public FixtureFactory getFixtureFactory() {
        return fixtureFactory;
    }

    
    /**
     * @param fixtureFactory the fixtureFactory to set
     */
    public void setFixtureFactory(FixtureFactory fixtureFactory) {
        this.fixtureFactory = fixtureFactory;
    }

    /**
     * @return the expressionFactory
     */
    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    
    /**
     * @param expressionFactory the expressionFactory to set
     */
    public void setExpressionFactory(ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
    }
}
