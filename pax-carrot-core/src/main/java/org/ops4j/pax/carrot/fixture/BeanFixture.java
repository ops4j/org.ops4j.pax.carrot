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

import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;

import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.el.ELExecutionContext;
import org.ops4j.pax.carrot.invocation.Invocation;
import org.ops4j.pax.carrot.invocation.NonStandardSetterInvocation;
import org.ops4j.pax.carrot.invocation.PropertyGetterInvocation;
import org.ops4j.pax.carrot.invocation.PropertySetterInvocation;
import org.ops4j.pax.carrot.invocation.SimpleMethodInvocation;

/**
 * Adapts any Java bean to the {@link Fixture} interface.
 * 
 * @author Harald Wellmann
 */
public class BeanFixture implements Fixture {

    private Object target;

    private ELExecutionContext context;

    private String fixtureName;

    public BeanFixture(Object target, ExecutionContext context) {
        this.target = target;
        this.context = (ELExecutionContext) context;
        this.fixtureName = "fixture";
        context.setVariable(fixtureName, target);
    }

    public BeanFixture(Object target, String name, ExecutionContext context) {
        this.target = target;
        this.context = (ELExecutionContext) context;
        this.fixtureName = name;
        context.setVariable(fixtureName, target);
    }

    public boolean canSet(String property) {
        ValueExpression expr = context.createPropertyValueExpression(fixtureName, property);
        try {
            return !expr.isReadOnly(context);
        }
        catch (PropertyNotFoundException exc) {
            return false;
        }
    }

    public boolean canGet(String property) {
        ValueExpression expr = context.createPropertyValueExpression(fixtureName, property);
        try {
            expr.getType(context);
            return true;
        }
        catch (PropertyNotFoundException exc) {
            return false;
        }
    }

    public boolean hasGetter(String property) {
        ValueExpression expr = context.createPropertyValueExpression(fixtureName, property);
        try {
            expr.getType(context);
            return true;
        }
        catch (PropertyNotFoundException exc) {
            return false;
        }
    }

    public boolean hasSimpleMethod(String methodName) {
        MethodExpression expr = context.createSimpleMethodExpression(fixtureName, methodName);
        try {
            MethodInfo methodInfo = expr.getMethodInfo(context);
            return methodInfo != null;
        }
        catch (MethodNotFoundException exc) {
            return false;
        }
    }

    public Invocation deferredGet(String property) {
        if (hasGetter(property)) {
            ValueExpression expr = context.createPropertyValueExpression(fixtureName, property);
            return new PropertyGetterInvocation(context, expr);
        }
        else {
            return deferredSimpleMethod(property);
        }
    }

    public Invocation deferredSet(String property) {
        ValueExpression expr = context.createPropertyValueExpression(fixtureName, property);
        return new PropertySetterInvocation(context, expr);
    }

    public Invocation deferredSimpleMethod(String methodName) {
        MethodExpression expr = context.createSimpleMethodExpression(fixtureName, methodName);
        return new SimpleMethodInvocation(context, expr);
    }

    public Invocation deferredNonStandardSet(String methodName) {
        MethodExpression expr = context.createNonStandardSetterExpression(fixtureName, methodName);
        return new NonStandardSetterInvocation(context, expr);
    }

    public Object getTarget() {
        return target;
    }

    public ExecutionContext getContext() {
        return context;
    }

    @Override
    public Fixture wrap(Object wrappedTarget, String name) {
        return new BeanFixture(wrappedTarget, name, context);
    } 
}
