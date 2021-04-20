package org.netcracker.learningcenter.h2.credentials.operations;

import org.netcracker.learningcenter.h2.credentials.entities.ConfluenceCredentials;
import org.netcracker.learningcenter.h2.credentials.daos.ConfluenceCredentialsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This class implements database operations with Confluence credentials
 *
 * @author Mikhail Savin
 */
@Service
public class ConfluenceOperations implements DatabaseCredentialsOperations<ConfluenceCredentials> {

    /**
     * ConfluenceCredentialsDAO instance to be able to perform database operations
     */
    private final ConfluenceCredentialsDAO confluenceCredentialsDAO;

    /**
     * Creates a ConfluenceOperations instance
     *
     * @param confluenceCredentialsDAO current ConfluenceCredentialsDAO instance
     */
    @Autowired
    public ConfluenceOperations(ConfluenceCredentialsDAO confluenceCredentialsDAO) {
        this.confluenceCredentialsDAO = confluenceCredentialsDAO;
    }

    /**
     * Adds Confluence credentials to database
     *
     * @param confluenceCredentials Confluence credentials to add (URL, token)
     */
    @Override
    public void add(ConfluenceCredentials confluenceCredentials) {
        ConfluenceCredentials credentialsFromDatabase = confluenceCredentialsDAO.
                findConfluenceCredentialsByUrl(confluenceCredentials.getUrl());
        if (credentialsFromDatabase == null) {
            confluenceCredentialsDAO.save(confluenceCredentials);
        }
    }

    /**
     * Removes Confluence credentials from database
     *
     * @param confluenceCredentials ConfluenceCredentials object to remove.
     *                        Actually, only Confluence URL is used when deleting
     */
    @Override
    @Transactional
    public void remove(ConfluenceCredentials confluenceCredentials) {
        confluenceCredentialsDAO.deleteByUrl(confluenceCredentials.getUrl());
    }

    /**
     * Updates existing Confluence credentials record in the database
     *
     * @param confluenceCredentials Confluence credentials to update
     */
    @Override
    public void update(ConfluenceCredentials confluenceCredentials) {
        ConfluenceCredentials credentialsFromDatabase = confluenceCredentialsDAO.
                findConfluenceCredentialsByUrl(confluenceCredentials.getUrl());
        if (credentialsFromDatabase != null) {
            confluenceCredentialsDAO.delete(credentialsFromDatabase);
            confluenceCredentialsDAO.save(confluenceCredentials);
        }
    }

    /**
     * Gets all existing Confluence credentials from database
     *
     * @return list of ConfluenceCredentials objects
     */
    @Override
    public List<ConfluenceCredentials> getAll() {
        return (List<ConfluenceCredentials>) confluenceCredentialsDAO.findAll();
    }

    /**
     * Gets Confluence credentials that match the specified URL
     *
     * @param url provided Confluence url
     * @return ConfluenceCredentials
     */
    public ConfluenceCredentials getConfluenceCredentialsByUrl(String url) {
        return confluenceCredentialsDAO.findConfluenceCredentialsByUrl(url);
    }
}
