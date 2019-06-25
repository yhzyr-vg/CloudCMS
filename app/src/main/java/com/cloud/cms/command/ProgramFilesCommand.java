package com.cloud.cms.command;

import java.io.Serializable;

/**
 * File: ProgramFilesCommand.java
 * Author: Landy
 * Create: 2019/6/10 15:58
 */
public class ProgramFilesCommand  implements Serializable {

    private String name;
    private String path;
    private long size;
    private boolean isDirectory;
    private boolean isFile;
    private String ext;
    private long modifyTime;

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }
}
