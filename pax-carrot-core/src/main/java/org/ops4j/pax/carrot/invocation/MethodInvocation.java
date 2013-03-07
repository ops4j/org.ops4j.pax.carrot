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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ops4j.pax.carrot.api.CarrotException;

/**
 * An {@Invocation} invoking a {@link Method} by reflection.
 * @author Harald Wellmann
 *
 */
public class MethodInvocation implements Invocation {

    private Object target;
    private Method method;

    public MethodInvocation(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public Object invoke(Object... args) {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalAccessException exc) {
            throw new CarrotException(exc);
        }
        catch (IllegalArgumentException exc) {
            throw new CarrotException(exc);
        }
        catch (InvocationTargetException exc) {
            throw new CarrotException(exc);
        }
    }
}
