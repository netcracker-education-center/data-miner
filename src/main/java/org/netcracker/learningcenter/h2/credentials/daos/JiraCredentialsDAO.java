package org.netcracker.learningcenter.h2.credentials.daos;

import org.netcracker.learningcenter.h2.credentials.entities.JiraCredentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for Jira credentials operations on database
 *
 * @author Mikhail Savin
 */
@Repository
public interface JiraCredentialsDAO extends CrudRepository<JiraCredentials, Long> {

    /**
     * Finds Jira credentials by URL.
     *
     * @param url Jira URL to search for
     * @return found Jira credentials
     */
    JiraCredentials findJiraCredentialsByUrl(String url);

    /**
     * Deletes Jira credentials by URL
     *
     * @param url Jira URL to delete by
     */
    void deleteByUrl(String url);
}
