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

package org.ops4j.pax.carrot.invocation;

import javax.el.ELContext;
import javax.el.MethodExpression;


/**
 * An {@link Invocation} of method with a single argument method by evaluating an EL
 * expression. Such methods are frequently used like setters in legacy fixture classes that
 * do not conform to the Java Beans naming convention.
 * 
 * @author Harald Wellmann
 * 
 */
public class NonStandardSetterInvocation implements Invocation {

    private ELContext context;
    private MethodExpression expr;

    public NonStandardSetterInvocation(ELContext context, MethodExpression expr) {
        this.context = context;
        this.expr = expr;
    }

    public Object invoke(Object... args) {
        assert args.length == 1;
        return expr.invoke(context, args);
    }
}
