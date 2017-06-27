package org.mongeez.commands;

import java.util.ArrayList;
import java.util.List;

public class GroupFile {

    private List<ChangeFileSet> groupFiles = new ArrayList<>();

    public void add(ChangeFileSet changeFileSet) {
        this.groupFiles.add(changeFileSet);
    }

    public List<ChangeFileSet> getGroupFiles() {
        return groupFiles;
    }

    public void setGroupFiles(List<ChangeFileSet> groupFiles) {
        this.groupFiles = groupFiles;
    }
}