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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.el.ValueExpression;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.ops4j.pax.carrot.el.MyPojo.Color;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;


public class FixtureELTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private ExpressionFactory factory;
    private SimpleContext context;
    private MyPojo myPojo;
    
    @Before
    public void setUp() {
        factory = new ExpressionFactoryImpl();
        context = new ELExecutionContext(factory, null);
        myPojo = new MyPojo();
        myPojo.setMyInt(17);
        myPojo.setMyString("foo");
        myPojo.myPublicString = "wide open";
        myPojo.setColor(Color.RED);
        myPojo.numItems(13);
        context.setVariable("fixture", factory.createValueExpression(myPojo, MyPojo.class));
    }

    @Test
    public void createExpression() {
        Object value = factory.createValueExpression(context, "#{fixture}", MyPojo.class).getValue(context);
        assertThat((MyPojo) value, is(sameInstance(myPojo)));
        
        ValueExpression myStringExpr = factory.createValueExpression(context, "#{fixture.myString}", String.class);
        Object myStringValue = myStringExpr.getValue(context);
        assertThat(myStringValue, is(instanceOf(String.class)));
        assertThat((String) myStringValue, is("foo"));
        
        myStringExpr.setValue(context, "bla");
        assertThat(myPojo.getMyString(), is("bla"));

        ValueExpression colorExpr = factory.createValueExpression(context, "#{fixture.color}", Color.class);
        Object colorValue = colorExpr.getValue(context);
        assertThat(colorValue, is(instanceOf(Color.class)));
        assertThat((Color) colorValue, is(Color.RED));

        colorExpr.setValue(context, "GREEN");
        assertThat(myPojo.getColor(), is(Color.GREEN));
    }

    @Test
    public void canInvokeNonStandardGetter() {
        MethodExpression methodExpr = factory.createMethodExpression(context, "#{fixture.numItems()}", Integer.class, new Class[0]);
        Object result = methodExpr.invoke(context, new Object[0]);
        assertThat(result, is(instanceOf(Integer.class)));
        assertThat((Integer) result, is(13));
    }
    
    @Test
    public void canInvokeNonStandardSetter() {
        context.setVariable("arg", factory.createValueExpression("17", Object.class));
        MethodExpression methodExpr = factory.createMethodExpression(context, "#{fixture.numItems(arg)}", Void.class, new Class[]{Object.class});
        Object result = methodExpr.invoke(context, new Object[0]);
        assertThat(result, is(nullValue()));
        assertThat(myPojo.numItems(), is(17));
    }
    
    @Test
    public void canHandlePublicField() {
        ValueExpression myStringExpr = factory.createValueExpression(context, "#{fixture.myPublicString}", String.class);
        Object myStringValue = myStringExpr.getValue(context);
        assertThat(myStringValue, is(instanceOf(String.class)));
        assertThat((String) myStringValue, is("wide open"));
    }
    
    @Test
    public void canInvokeSimpleMethod() {
        MethodExpression methodExpr = factory.createMethodExpression(context, "#{fixture.toString()}", String.class, new Class[0]);
        Object result = methodExpr.invoke(context, new Object[0]);
        assertThat(result, is(instanceOf(String.class)));
        assertThat((String) result, is("MyPojo [myString=foo]"));
    }

    @Test
    public void cannotInvokeNonExistingSimpleMethod() {
        MethodExpression methodExpr = factory.createMethodExpression(context, "#{fixture.doesNotExist()}", Object.class, new Class[0]);
        
        thrown.expect(MethodNotFoundException.class);
        methodExpr.invoke(context, new Object[0]);
    }
    
    @Test
    public void checkFilterExpression() {
        ValueExpression expr = factory.createValueExpression(context, 
            "#{fixture.myInt == 17 and fixture.myString == 'foo' and fixture.color == 'RED'}", 
            Boolean.class);
        Object value = expr.getValue(context);
        assertThat((Boolean) value, is(true));
    }
    
    @Test
    public void checkMapValueExpression() {
        Map<String,String> map = new HashMap<String, String>();
        map.put("foo", "bar");
        ValueExpression expr = factory.createValueExpression(map, Map.class);
        context.setVariable("row", expr);
        ValueExpression valueExpr = factory.createValueExpression(context, "#{row.foo}", String.class);
        Object result = valueExpr.getValue(context);
        assertThat(result, is(instanceOf(String.class)));
        assertThat((String) result, is("bar"));
        
    }

}
