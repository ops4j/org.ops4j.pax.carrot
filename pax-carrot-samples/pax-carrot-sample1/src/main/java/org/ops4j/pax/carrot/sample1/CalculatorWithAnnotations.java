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

package org.ops4j.pax.carrot.sample1;

import javax.inject.Named;

import org.ops4j.pax.carrot.annotation.BeforeFirstExpectation;

@Named
public class CalculatorWithAnnotations {

    private int a;
    private int b;
    
    private int sum;
    private int difference;
    private int product;
    private int quotient;
    

    public int getSum() {
        return sum;
    }

    public int getDifference() {
        return difference;
    }

    public int getProduct() {
        return product;
    }

    public int getQuotient() {
        return quotient;
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
    
    @BeforeFirstExpectation
    public void calculate() {
        sum = a + b;
        difference = a - b;
        product = a * b;
        quotient = a / b;
    }
}
