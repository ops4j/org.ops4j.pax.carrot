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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ops4j.pax.carrot.api.CarrotException;
import org.ops4j.pax.carrot.api.ExecutionContextFactory;
import org.ops4j.pax.carrot.runner.FileRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;


/**
 * @author Harald Wellmann
 *
 */
@RequestScoped
@Named
public class RunnerController {
    
    private static Logger log = LoggerFactory.getLogger(RunnerController.class);

    @Inject
    private TreeBean treeBean;
    
    @Inject
    private Settings settings;
    
    @Inject
    private ExecutionContextFactory ecf;

    private File file;

    private String content;
    
    
    public String runSelectedTest() {
        String test = treeBean.getRelativePath();
        
        log.info("running {}", test);
        
        File inputDir = new File(settings.getInputRoot());
        File outputDir = new File(settings.getOutputRoot());
        File inputFile = new File(inputDir, test);
        if (!inputFile.exists() || inputFile.isDirectory()) {
            return "run";
        }
        
        FileRunner runner = new FileRunner(ecf.newInstance(null), inputDir, outputDir, test);
        runner.run();
        return "run";
    }
    
    public void loadFile() {
        file = new File(settings.getOutputRoot(), treeBean.getRelativePath());
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        }
        content = "";
        try {
            content = Files.toString(file, Charset.forName("UTF-8"));
        }
        catch (IOException exc) {
            throw new CarrotException(exc);
        }
    }
    
    /**
     * @return the content
     */
    public String getOutput() {
        if (file == null) {
            loadFile();
        }
        return content;
    }
}
