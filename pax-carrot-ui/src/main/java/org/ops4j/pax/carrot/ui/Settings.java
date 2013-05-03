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

package org.ops4j.pax.carrot.ui;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.ops4j.pax.carrot.api.CarrotException;


/**
 * @author Harald Wellmann
 *
 */
@ApplicationScoped
public class Settings {
    
    private String inputRoot;
    
    private String outputRoot;
    
    @PostConstruct
    public void loadProperties() {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/carrot.properties"));
            inputRoot = props.getProperty("pax.carrot.input.root");
            outputRoot = props.getProperty("pax.carrot.output.root");
        }
        catch (IOException exc) {
            throw new CarrotException(exc);
        }
    }

    
    /**
     * @return the inputRoot
     */
    public String getInputRoot() {
        return inputRoot;
    }

    
    /**
     * @param inputRoot the inputRoot to set
     */
    public void setInputRoot(String inputRoot) {
        this.inputRoot = inputRoot;
    }

    
    /**
     * @return the outputRoot
     */
    public String getOutputRoot() {
        return outputRoot;
    }

    
    /**
     * @param outputRoot the outputRoot to set
     */
    public void setOutputRoot(String outputRoot) {
        this.outputRoot = outputRoot;
    }
}
