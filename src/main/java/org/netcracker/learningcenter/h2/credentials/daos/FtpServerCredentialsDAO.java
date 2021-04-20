package org.netcracker.learningcenter.h2.credentials.daos;

import org.netcracker.learningcenter.h2.credentials.entities.FtpServerCredentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface for Jira credentials operations on database
 *
 * @author Mikhail Savin
 */
@Repository
public interface FtpServerCredentialsDAO extends CrudRepository<FtpServerCredentials, Long> {

    /**
     * Finds FTP-server credentials by server IP address.
     *
     * @param server FTP-server IP to search for
     * @return a list of found FTP-server credentials
     */
    List<FtpServerCredentials> findFtpServerCredentialsByServer(String server);

    /**
     * Finds FTP-server credentials by server IP address and port.
     *
     * @param server FTP-server IP to search for
     * @param port FTP-server port number to search for
     * @return found FTP-server credentials
     */
    FtpServerCredentials findFtpServerCredentialsByServerAndPort(String server, int port);

    /**
     * Deletes FTP-server credentials by FTP-server IP
     *
     * @param server FTP-server IP to delete by
     */
    void deleteByServer(String server);
}
