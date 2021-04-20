package org.netcracker.learningcenter.h2.credentials.operations;

import org.netcracker.learningcenter.h2.credentials.entities.Credentials;

import java.util.List;

/**
 * Interface for database credentials operations
 *
 * @param <T> class that implements Credentials interface
 * @author Mikhail Savin
 */
public interface DatabaseCredentialsOperations<T extends Credentials> {

    /**
     * Adds credentials to database
     *
     * @param credentials credentials to add
     */
    void add(T credentials);

    /**
     * Removes credentials from database
     *
     * @param credentials object to remove.
     */
    void remove(T credentials);

    /**
     * Updates existing credentials record in the database
     *
     * @param credentials credentials to update
     */
    void update(T credentials);

    /**
     * Gets all existing credentials from database
     *
     * @return list of credentials
     */
    List<T> getAll();
}
