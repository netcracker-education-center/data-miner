package org.netcracker.learningcenter.services;

import org.apache.commons.net.ftp.FTPFileFilter;
import org.netcracker.learningcenter.reader.ReaderFactory;
import org.netcracker.learningcenter.utils.FTPFileData;
import org.netcracker.learningcenter.utils.FileUtils;
import org.netcracker.learningcenter.utils.FtpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class FTPService {
    @Autowired
    private ReaderFactory readerFactory;

    /**
     * Method of obtaining data from files downloaded from the FTP server
     *
     * @param client  FTP client for connecting to the FTP server and working with files on it
     * @param path    path to the directory on the FTP server where to look for files
     * @param filters filters that can be applied when searching for files on the FTP server
     * @return list of FTPFileData objects that contain data received from files from the FTP server
     */
    public List<FTPFileData> getDataFromFiles(FtpClient client, String path, List<FTPFileFilter> filters) throws Exception {
        List<FTPFileData> fileInfo = new ArrayList<>();
        try (FtpClient c = client) {
            c.open();
            File tmpDir = Files.createTempDirectory("tmpFtpStorage").toFile();
            String tmpFtpStorage = tmpDir.getAbsolutePath();
            c.downloadFiles(path, filters, tmpFtpStorage);
            List<File> list = FileUtils.listFilesForFolder(tmpFtpStorage);
            for (File f : list) {
                FTPFileData file = new FTPFileData();
                file.setFilename(f.getName());
                file.setServer(c.getServer());
                file.setText(readerFactory.getReader(f.getName()).read(f));
                file.setModificationDate(
                        Instant.ofEpochMilli(f.lastModified())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate());
                fileInfo.add(file);
                f.delete();
            }
            tmpDir.delete();
        }
        return fileInfo;
    }
}
