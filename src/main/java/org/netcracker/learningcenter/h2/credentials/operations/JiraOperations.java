package org.netcracker.learningcenter.h2.credentials.operations;

import org.jasypt.util.text.AES256TextEncryptor;
import org.netcracker.learningcenter.h2.credentials.daos.JiraCredentialsDAO;
import org.netcracker.learningcenter.h2.credentials.entities.JiraCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This class implements database operations with Jira credentials
 *
 * @author Mikhail Savin
 */
@Service
public class JiraOperations implements DatabaseCredentialsOperations<JiraCredentials> {

    /**
     * JiraCredentialsDAO instance to be able to perform database operations
     */
    private final JiraCredentialsDAO jiraCredentialsDAO;

    /**
     * AES256TextEncryptor instance to encrypt passwords
     */
    private final AES256TextEncryptor aes256TextEncryptor;

    /**
     * Creates a JiraOperations instance
     *
     * @param jiraCredentialsDAO current JiraCredentialsDAO instance
     * @param aes256TextEncryptor current AES256TextEncryptor instance
     */
    @Autowired
    public JiraOperations(JiraCredentialsDAO jiraCredentialsDAO, AES256TextEncryptor aes256TextEncryptor) {
        this.jiraCredentialsDAO = jiraCredentialsDAO;
        this.aes256TextEncryptor = aes256TextEncryptor;
    }

    /**
     * Adds Jira credentials to database
     *
     * @param jiraCredentials Jira credentials to add (URL, login, password)
     */
    @Override
    public void add(JiraCredentials jiraCredentials) {
        JiraCredentials credentialsFromDatabase = jiraCredentialsDAO.
                findJiraCredentialsByUrl(jiraCredentials.getUrl());
        if (credentialsFromDatabase == null) {
            jiraCredentials.setPassword(aes256TextEncryptor.encrypt(jiraCredentials.getPassword()));
            jiraCredentialsDAO.save(jiraCredentials);
        }
    }

    /**
     * Removes Jira credentials from database
     *
     * @param jiraCredentials JiraCredentials object to remove.
     *                        Actually, only Jira URL is used when deleting
     */
    @Override
    @Transactional
    public void remove(JiraCredentials jiraCredentials) {
        jiraCredentialsDAO.deleteByUrl(jiraCredentials.getUrl());
    }

    /**
     * Updates existing Jira credentials record in the database
     *
     * @param jiraCredentials Jira credentials to update
     */
    @Override
    public void update(JiraCredentials jiraCredentials) {
        JiraCredentials credentialsFromDatabase = jiraCredentialsDAO.
                findJiraCredentialsByUrl(jiraCredentials.getUrl());
        if (credentialsFromDatabase != null) {
            jiraCredentialsDAO.delete(credentialsFromDatabase);
            jiraCredentials.setPassword(aes256TextEncryptor.encrypt(jiraCredentials.getPassword()));
            jiraCredentialsDAO.save(jiraCredentials);
        }
    }

    /**
     * Gets all existing Jira credentials from database
     *
     * @return list of JiraCredentials objects
     */
    @Override
    public List<JiraCredentials> getAll() {
        return (List<JiraCredentials>) jiraCredentialsDAO.findAll();
    }

    /**
     * Gets Jira credentials that match the specified URL
     *
     * @param url provided Jira url
     * @return JiraCredentials
     */
    public JiraCredentials getJiraCredentialsByUrl(String url) {
        return jiraCredentialsDAO.findJiraCredentialsByUrl(url);
    }
}
