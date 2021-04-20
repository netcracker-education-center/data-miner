package org.netcracker.learningcenter.utils;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.util.List;

public class FtpClient implements AutoCloseable {
    private String server;
    private int port;
    private String user;
    private String password;
    private FTPClient ftp;


    public FtpClient(String server, int port, String user, String password) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void open() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);
        ftp.enterLocalPassiveMode();
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(user, password);

    }

    private void downloadFile(String source, FTPFile file, String pathToDownload) throws IOException {
        File downloadFile = FileUtils.createFile(pathToDownload, file.getName());
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(downloadFile))) {
            ftp.retrieveFile(source, os);
        }
        downloadFile.setLastModified(file.getTimestamp().getTimeInMillis());
    }

    /**
     * Method downloads files from the FTP server
     *
     * @param path           path to the directory on the FTP server from which to download files
     * @param filters        a list of filters by which the file is checked before downloading
     * @param pathToDownload path to the directory where you want to place the downloaded files
     */
    public void downloadFiles(String path, List<FTPFileFilter> filters, String pathToDownload) throws IOException {
        FTPFile[] remoteFiles = ftp.listFiles(path);
        for (FTPFile remoteFile : remoteFiles) {
            if (!remoteFile.getName().equals(".") && !remoteFile.getName().equals("..")) {
                String remoteFilePath = path + "/" + remoteFile.getName();
                if (remoteFile.isDirectory()) {
                    downloadFiles(remoteFilePath, filters, pathToDownload);
                } else {
                    if (isSuitableByFilters(filters, remoteFile)) {
                        downloadFile(remoteFilePath, remoteFile, pathToDownload);
                    }
                }
            }
        }
    }

    /**
     * The method checks the file against the specified filters
     *
     * @param filters    list of filters by which the file is checked
     * @param remoteFile file being checked
     * @return true if the file matches all filters
     */
    private boolean isSuitableByFilters(List<FTPFileFilter> filters, FTPFile remoteFile) {
        for (FTPFileFilter filter : filters) {
            if (!filter.accept(remoteFile)) {
                return false;
            }
        }
        return true;
    }

    public String getServer() {
        return server;
    }

    @Override
    public void close() throws Exception {
        ftp.logout();
        ftp.disconnect();
    }
}
