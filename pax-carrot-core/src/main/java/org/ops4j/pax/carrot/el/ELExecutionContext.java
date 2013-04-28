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

package org.ops4j.pax.carrot.el;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.FixtureFactory;
import org.ops4j.pax.carrot.api.InterpreterSelector;
import org.ops4j.pax.carrot.api.Statistics;

/**
 * Provides an {@link ExecutionContext} for variables, delegating to a {@link javax.el.ELConext}.
 * <p>
 * NOTE: At the moment, this may seem a little overengineered, but using EL will allow us to
 * work with more complex EL expressions for accessing properties deeply nested in an object
 * graph.
 * 
 * @author Harald Wellmann
 *
 */
public class ELExecutionContext extends CarrotELContext implements ExecutionContext {

    private ExpressionFactory factory;
    private FixtureFactory fixtureFactory;
    private InterpreterSelector interpreterSelector;
    private boolean stopOnFirstFailure;

    public ELExecutionContext(ExpressionFactory factory, FixtureFactory fixtureFactory, InterpreterSelector interpreterSelector) {
        this.factory = factory;
        this.fixtureFactory = fixtureFactory;
        this.interpreterSelector = interpreterSelector;
    }

    public void setVariable(String symbol, Object value) {
        ValueExpression expr = factory.createValueExpression(value, Object.class);
        super.setVariable(symbol, expr);
    }

    public Object getVariable(String symbol) {
        ValueExpression expr = getVariableMapper().resolveVariable(symbol);
        return (expr == null) ? null : expr.getValue(this);
    }

    public ValueExpression createPropertyValueExpression(String base, String property) {
        String expr = String.format("#{%s.%s}", base, property);
        return factory.createValueExpression(this, expr, Object.class);
    }

    public ValueExpression createFilterValueExpression(String expr) {
        return factory.createValueExpression(this, expr, Boolean.class);
    }

    public MethodExpression createSimpleMethodExpression(String base, String methodName) {
        String expr = String.format("#{%s.%s()}", base, methodName);
        return factory.createMethodExpression(this, expr, Object.class, new Class[0]);
    }

    public MethodExpression createNonStandardSetterExpression(String base, String methodName) {
        String expr = String.format("#{%s.%s(arg)}", base, methodName);
        return factory.createMethodExpression(this, expr, Void.class, new Class[]{ Object.class });
    }

    @Override
    public FixtureFactory getFixtureFactory() {
        return fixtureFactory;
    }

    @Override
    public void setStopOnFirstFailure(boolean stop) {
        stopOnFirstFailure = stop;
    }

    @Override
    public boolean shouldStop(Statistics stats) {
        return stopOnFirstFailure && stats.hasFailed();
    }

    @Override
    public boolean canContinue(Statistics stats) {
        return !shouldStop(stats);
    }

    @Override
    public InterpreterSelector getInterpreterSelector() {
        return interpreterSelector;
    }
}
