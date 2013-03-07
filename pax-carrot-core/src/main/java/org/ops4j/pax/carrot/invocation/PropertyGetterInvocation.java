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
import javax.el.ValueExpression;


/**
 * An {@link Invocation} getting a property value by evaluating an EL expression.
 * 
 * @author Harald Wellmann
 *
 */
public class PropertyGetterInvocation implements Invocation {

    private ELContext context;
    private ValueExpression expr;

    public PropertyGetterInvocation(ELContext context, ValueExpression expr) {
        this.context = context;
        this.expr = expr;
    }

    public Object invoke(Object... args) {
        return expr.getValue(context);
    }
}
