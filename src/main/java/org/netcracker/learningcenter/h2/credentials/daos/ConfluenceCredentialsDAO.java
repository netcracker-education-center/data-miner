package org.netcracker.learningcenter.h2.credentials.daos;

import org.netcracker.learningcenter.h2.credentials.entities.ConfluenceCredentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for Confluence credentials operations on database
 *
 * @author Mikhail Savin
 */
@Repository
public interface ConfluenceCredentialsDAO extends CrudRepository<ConfluenceCredentials, Long> {

    /**
     * Finds Confluence credentials by URL.
     *
     * @param url Confluence URL to search for
     * @return found Confluence credentials
     */
    ConfluenceCredentials findConfluenceCredentialsByUrl(String url);

    /**
     * Deletes Confluence credentials by URL
     *
     * @param url Confluence URL to delete by
     */
    void deleteByUrl(String url);
}
