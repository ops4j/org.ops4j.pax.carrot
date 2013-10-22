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

package org.ops4j.pax.carrot.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * A test execution listener holding the current application context.
 * 
 * @author Harald Wellmann
 * @version $Rev: 53870 $ $Date: 2013-02-12 11:32:44 +0100 (Di, 12. Feb 2013) $
 * @since 22.10.2013
 */
public class ApplicationContextHolder implements TestExecutionListener {

    private ApplicationContext applicationContext;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        this.applicationContext = testContext.getApplicationContext();
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
    }

    /**
     * Gets the application context for the current test.
     * 
     * @return Returns the {@link #applicationContext}.
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
