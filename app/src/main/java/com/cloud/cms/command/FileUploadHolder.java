package com.cloud.cms.command;

import java.io.BufferedOutputStream;
import java.io.File;

public class FileUploadHolder {
    private String fileName;

    public File getRecievedFile() {
        return recievedFile;
    }

    public void setRecievedFile(File recievedFile) {
        this.recievedFile = recievedFile;
    }

    private File recievedFile;

    public BufferedOutputStream getFileOutPutStream() {
        return fileOutPutStream;
    }

    public void setFileOutPutStream(BufferedOutputStream fileOutPutStream) {
        this.fileOutPutStream = fileOutPutStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void reset() {
        fileName = null;
        fileOutPutStream = null;
    }

    private BufferedOutputStream fileOutPutStream;
}
