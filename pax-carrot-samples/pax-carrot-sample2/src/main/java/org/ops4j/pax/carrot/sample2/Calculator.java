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

package org.ops4j.pax.carrot.sample2;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CalculatorSpringConfig.class)
public class Calculator {
    
    @Inject
    private Operation addition;

    @Inject
    private Operation subtraction;
    
    @Inject
    private Operation multiplication;
    
    @Inject
    private Operation division;

    private int a;
    private int b;

    public int getSum() {
        return addition.compose(a, b);
    }

    public int getDifference() {
        return addition.compose(a, b);
    }

    public int getProduct() {
        return multiplication.compose(a, b);
    }

    public int getQuotient() {
        return division.compose(a, b);
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
