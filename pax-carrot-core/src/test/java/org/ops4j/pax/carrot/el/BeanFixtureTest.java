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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.el.ExpressionFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.ops4j.pax.carrot.el.MyPojo.Color;
import org.ops4j.pax.carrot.fixture.BeanFixture;


public class BeanFixtureTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private ExpressionFactory factory;
    private CarrotELContext context;
    private MyPojo myPojo;

    private BeanFixture fixture;
    
    @Before
    public void setUp() {
        factory = ExpressionFactory.newInstance();
        context = new CarrotELContext();
        myPojo = new MyPojo();
        myPojo.setMyInt(17);
        myPojo.setMyString("foo");
        myPojo.myPublicString = "wide open";
        myPojo.setColor(Color.RED);
        myPojo.numItems(13);
        context.setVariable("fixture", factory.createValueExpression(myPojo, MyPojo.class));
        ELExecutionContext executionContext = new ELExecutionContext(factory, null, null, null);
        fixture = new BeanFixture(myPojo, executionContext);
    }

    @Test
    public void canGetExistingProperty() {
        assertThat(fixture.canGet("myString"), is(true));
    }

    @Test
    public void cannotGetNonExistingProperty() {
        assertThat(fixture.canGet("foo"), is(false));
    }
    
    @Test
    public void canSetExistingProperty() {
        assertThat(fixture.canSet("myString"), is(true));
    }

    @Test
    public void cannotSetNonExistingProperty() {
        assertThat(fixture.canSet("foo"), is(false));
    }
    
    @Test
    public void shouldFindSimpleMethod() {
        assertThat(fixture.hasSimpleMethod("name"), is(true));
    }
    
    @Test
    public void shouldNotFindSimpleMethod() {
        assertThat(fixture.hasSimpleMethod("address"), is(false));
    }
}
