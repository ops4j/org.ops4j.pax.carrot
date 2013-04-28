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

package org.ops4j.pax.carrot.osgi;

import java.util.HashMap;
import java.util.Map;

import org.ops4j.pax.carrot.api.ExecutionContext;
import org.ops4j.pax.carrot.api.FixtureFactory;
import org.ops4j.pax.carrot.fixture.BeanFixture;
import org.ops4j.pax.carrot.fixture.Fixture;



/**
 * @author Harald Wellmann
 *
 */
public class OsgiFixtureLoader implements FixtureFactory {

    private ExecutionContext context;

    private Map<String, Object> fixtureMap = new HashMap<String, Object>();
    
    protected synchronized void addFixture(Object fixture, Map<String, String> props) {
        String name = props.get("pax.carrot.fixture.name");
        System.out.println("adding fixture " + name);
        fixtureMap.put(name, fixture);        
    }
    
    protected synchronized void removeFixture(Object fixture, Map<String, String> props) {
        String name = props.get("pax.carrot.fixture.name");
        fixtureMap.remove(name);        
    }
    
    @Override
    public synchronized Fixture createFixture(String fixtureName) {
        Object target = fixtureMap.get(fixtureName);
        return new BeanFixture(target, context);
    }

    @Override
    public void setContext(ExecutionContext context) {
        this.context = context;
    }
}
