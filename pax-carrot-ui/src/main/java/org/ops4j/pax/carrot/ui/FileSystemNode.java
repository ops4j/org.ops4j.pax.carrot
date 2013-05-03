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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Harald Wellmann
 * 
 */
public class FileSystemNode implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<FileSystemNode> directories;
    private List<FileSystemNode> files;

    private String root;
    private File file;

    private boolean expanded;

    public FileSystemNode(String root, String path) {
        this.root = root;
        this.file = new File(path);
    }

    public synchronized List<FileSystemNode> getDirectories() {

        if (directories == null) {
            loadChildren();
        }
        return directories;
    }

    public synchronized List<FileSystemNode> getFiles() {

        if (files == null) {
            loadChildren();
        }
        return files;
    }
    
    private void loadChildren() {
        directories = new ArrayList<FileSystemNode>();
        files = new ArrayList<FileSystemNode>();
        String[] children = file.list();
        Arrays.sort(children);
        for (String name : children) {
            File child = new File(file, name);
            FileSystemNode childNode = new FileSystemNode(root, child.getPath());
            if (child.isDirectory()) {
                directories.add(childNode);
            }
            else {
                files.add(childNode);
            }
        }
    }

    public String getShortPath() {
        return file.getName();
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @return the expanded
     */
    public boolean getExpanded() {
        return expanded;
    }

    /**
     * @param expanded
     *            the expanded to set
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getRelativePath() {
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.equals(root)) {
            return "";
        }
        else {
            return absolutePath.substring(root.length() + 1);
        }
    }
}
