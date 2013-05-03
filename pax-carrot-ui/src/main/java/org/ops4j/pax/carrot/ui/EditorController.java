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

import com.google.common.io.Files;

/**
 * @author Harald Wellmann
 * 
 */
@RequestScoped
@Named("editor")
public class EditorController {

    private static final Charset utf8 = Charset.forName("UTF-8");
    
    @Inject
    private TreeBean treeBean;

    private File file;
    private String content = "";

    public void loadFile() {
        file = treeBean.getSelectedFile();
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        }
        content = "";
        try {
            content = Files.toString(file, utf8);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @return the content
     */
    public String getContent() {
        if (file == null) {
            loadFile();
        }
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    public void save() {

        file = treeBean.getSelectedFile();
        try {
            Files.write(content, file, utf8);
        }
        catch (IOException exc) {
            throw new CarrotException(exc);
        }
    }

    public String cancel() {
        return "browse";
    }
}
