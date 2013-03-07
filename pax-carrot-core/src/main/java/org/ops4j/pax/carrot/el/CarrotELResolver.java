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

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;

/**
 * A {@link CompositeELResolver} which inserts a {@link PublicFieldELResolver} into the resolver chain.
 * The public field takes precedence over the corresponding bean property accessor.
 * <p>
 * Example: For a class  
 * <pre>
 * public class Foo {
 *     public String bar;
 *     
 *     public String getBar() {
 *         return bar;
 *     }
 *     
 *     public void setBar(String bar) {
 *         this.bar = bar;
 *     }
 * }
 * </pre>
 * the expression {@code #{foo.bar}} will be evaluated using the public field and not the 
 * getter and setter methods (which may not be present at all).
 *      
 * @author Harald Wellmann
 *
 */
public class CarrotELResolver extends CompositeELResolver {

    public CarrotELResolver() {
        add(new ArrayELResolver(false));
        add(new ListELResolver(false));
        add(new MapELResolver(false));
        add(new ResourceBundleELResolver());
        add(new PublicFieldELResolver());
        add(new BeanELResolver(false));
    }
}
