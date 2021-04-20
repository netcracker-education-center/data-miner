package org.netcracker.learningcenter.filter;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import java.util.List;

public class ExtensionFilter implements FTPFileFilter {
    private List<String> extensions;

    public ExtensionFilter(List<String> extensions) {
        this.extensions = extensions;
    }

    @Override
    public boolean accept(FTPFile ftpFile) {
        for (String extension : extensions) {
            if (ftpFile.getName().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
