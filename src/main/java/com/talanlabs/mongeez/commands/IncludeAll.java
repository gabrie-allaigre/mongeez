package com.talanlabs.mongeez.commands;

public class IncludeAll {

    private String path;
    private boolean relativeToChangelogFile;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRelativeToChangelogFile() {
        return relativeToChangelogFile;
    }

    public void setRelativeToChangelogFile(boolean relativeToChangelogFile) {
        this.relativeToChangelogFile = relativeToChangelogFile;
    }
}
