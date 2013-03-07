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

package org.ops4j.pax.carrot.step;

import static org.hamcrest.CoreMatchers.equalTo;

import org.hamcrest.Matcher;
import org.ops4j.pax.carrot.invocation.Invocation;

/**
 * A Step executes an {@link Invocation}, stores its {@link Result} and matches it
 * with an expectation.
 * 
 * @author Harald Wellmann
 *
 */
public class Step {

    private Invocation invocation;
    private Matcher<?> matcher;
    private Result result;

    public Step(Invocation invocation) {
        this.invocation = invocation;
    }

    public Object execute(String... args) {
        result = new Result(matcher);
        try {
            Object actual = invocation.invoke((Object[]) args);
            result.setActual(actual);
        }
        // CHECKSTYLE:SKIP
        catch (Throwable exc) {
            result.setException(exc.getCause());
        }

        return result.getActual();
    }

    public void expect(Matcher<?> expected) {
        this.matcher = expected;
    }

    public void expect(String value) {
        expect(equalTo(value));
    }

    public Result getResult() {
        return result;
    }

    public Throwable getException() {
        return result.getException();
    }
}
