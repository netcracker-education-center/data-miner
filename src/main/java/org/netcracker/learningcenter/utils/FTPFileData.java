package org.netcracker.learningcenter.utils;

import java.time.LocalDate;

/**
 * Class data model received from a file from the FTP server
 */
public class FTPFileData {
    private String filename;
    private String server;
    private LocalDate modificationDate;
    private String text;

    public FTPFileData() {
    }

    public FTPFileData(String filename, String server, LocalDate fileModificationDate, String text) {
        this.filename = filename;
        this.server = server;
        this.modificationDate = fileModificationDate;
        this.text = text;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public LocalDate getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDate modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
