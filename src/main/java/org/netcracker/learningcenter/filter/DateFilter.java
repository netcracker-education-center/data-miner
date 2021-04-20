package org.netcracker.learningcenter.filter;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateFilter implements FTPFileFilter {
    private LocalDate requiredDate;

    public DateFilter(String requiredDate) {
        this.requiredDate = LocalDate.parse(requiredDate);
    }

    @Override
    public boolean accept(FTPFile ftpFile) {
        LocalDate fileModificationDate = ftpFile.getTimestamp().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        return fileModificationDate.isEqual(requiredDate);
    }
}
