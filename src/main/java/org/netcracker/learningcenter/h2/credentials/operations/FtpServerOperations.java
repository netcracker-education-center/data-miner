package org.netcracker.learningcenter.h2.credentials.operations;

import org.jasypt.util.text.AES256TextEncryptor;
import org.netcracker.learningcenter.h2.credentials.daos.FtpServerCredentialsDAO;
import org.netcracker.learningcenter.h2.credentials.entities.FtpServerCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This class implements database operations with FTP-server credentials
 *
 * @author Mikhail Savin
 */
@Service
public class FtpServerOperations implements DatabaseCredentialsOperations<FtpServerCredentials> {

    /**
     * FtpServerCredentialsDAO instance to be able to perform database operations
     */
    private final FtpServerCredentialsDAO ftpServerCredentialsDAO;

    /**
     * AES256TextEncryptor instance to encrypt passwords
     */
    private final AES256TextEncryptor aes256TextEncryptor;

    /**
     * Creates a FtpServerOperations instance
     *
     * @param ftpServerCredentialsDAO current FtpServerCredentialsDAO instance
     * @param aes256TextEncryptor current AES256TextEncryptor instance
     */
    @Autowired
    public FtpServerOperations(FtpServerCredentialsDAO ftpServerCredentialsDAO,
                               AES256TextEncryptor aes256TextEncryptor) {
        this.ftpServerCredentialsDAO = ftpServerCredentialsDAO;
        this.aes256TextEncryptor = aes256TextEncryptor;
    }

    /**
     * Adds FTP-server credentials to database
     *
     * @param ftpServerCredentials FTP-server credentials to add (server ip, port, login, password)
     */
    @Override
    public void add(FtpServerCredentials ftpServerCredentials) {
        FtpServerCredentials credentialsFromDatabase = ftpServerCredentialsDAO.
                findFtpServerCredentialsByServerAndPort(ftpServerCredentials.getServer(), ftpServerCredentials.getPort());
        if (credentialsFromDatabase == null) {
            ftpServerCredentials.setPassword(aes256TextEncryptor.encrypt(ftpServerCredentials.getPassword()));
            ftpServerCredentialsDAO.save(ftpServerCredentials);
        }
    }

    /**
     * Removes FTP-server credentials from database
     *
     * @param ftpServerCredentials FTPServerCredentials object to remove.
     *                             Actually, only FTP-server ip address ("server" field) is used when deleting
     */
    @Override
    @Transactional
    public void remove(FtpServerCredentials ftpServerCredentials) {
        ftpServerCredentialsDAO.deleteByServer(ftpServerCredentials.getServer());
    }

    /**
     * Updates existing FTP-server credentials record in the database
     *
     * @param ftpServerCredentials FTP-server credentials to update
     */
    @Override
    public void update(FtpServerCredentials ftpServerCredentials) {
        FtpServerCredentials credentialsFromDatabase = ftpServerCredentialsDAO.
                findFtpServerCredentialsByServerAndPort(ftpServerCredentials.getServer(), ftpServerCredentials.getPort());
        if (credentialsFromDatabase != null) {
            ftpServerCredentialsDAO.delete(credentialsFromDatabase);
            ftpServerCredentials.setPassword(aes256TextEncryptor.encrypt(ftpServerCredentials.getPassword()));
            ftpServerCredentialsDAO.save(ftpServerCredentials);
        }
    }

    /**
     * Gets all existing FTP-server credentials from database
     *
     * @return list of FtpServerCredentials objects
     */
    @Override
    public List<FtpServerCredentials> getAll() {
        return (List<FtpServerCredentials>) ftpServerCredentialsDAO.findAll();
    }

    /**
     * Gets FTP-server credentials that match the specified server ip address
     *
     * @param server provided Server ip
     * @return FtpServerCredentials
     */
    public List<FtpServerCredentials> getFtpServerCredentialsByServer(String server) {
        return ftpServerCredentialsDAO.findFtpServerCredentialsByServer(server);
    }
}
