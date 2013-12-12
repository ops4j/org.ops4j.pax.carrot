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

@Named
public class FitCalculator {

    public int a;
    public int b;

    public int sum() {
        return a + b;
    }

    public int difference() {
        return a + b;
    }

    public int product() {
        return a * b;
    }

    public int quotient() {
        return a / b;
    }
}
