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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.richfaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 * 
 */
@SessionScoped
@Named
public class TreeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static Logger log = LoggerFactory.getLogger(TreeBean.class);

    @Inject
    private Settings settings;

    private Collection<TreeNode> currentSelection;

    private FileSystemNode root;
    
    private File selectedFile;
    
    @PostConstruct
    public void init() {
        String inputRoot = settings.getInputRoot();
        root = new FileSystemNode(inputRoot, inputRoot);
        
    }

    public List<FileSystemNode> getRoots() {
        return Collections.singletonList(root);
    }

    public void selectionChanged(TreeSelectionChangeEvent event) {
        Collection<Object> selection = event.getNewSelection();
        if (selection.isEmpty()) {
            selectedFile = null;
        }
        else {
            Object currentSelectionKey = selection.iterator().next();
            UITree tree = (UITree) event.getSource();
            tree.setRowKey(currentSelectionKey);
            FileSystemNode selectedNode = (FileSystemNode) tree.getRowData();
            selectedFile = selectedNode.getFile();
            log.info("selected {}", selectedFile);
        }
    }

    /**
     * @return the currentSelection
     */
    public Collection<TreeNode> getCurrentSelection() {
        return currentSelection;
    }

    /**
     * @param currentSelection
     *            the currentSelection to set
     */
    public void setCurrentSelection(Collection<TreeNode> currentSelection) {
        this.currentSelection = currentSelection;
    }

    
    /**
     * @return the selectedFile
     */
    public File getSelectedFile() {
        return selectedFile;
    }

    
    /**
     * @param selectedFile the selectedFile to set
     */
    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public String getRelativePath() {
        if (selectedFile == null) {
            return "";
        }
        
        String absolutePath = selectedFile.getAbsolutePath();
        String absoluteRoot = root.getFile().getAbsolutePath();
        if (absolutePath.equals(absoluteRoot)) {
            return "";
        }

        return absolutePath.substring(absoluteRoot.length() + 1);
    }   
}
