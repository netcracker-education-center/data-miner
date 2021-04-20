package org.netcracker.learningcenter.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;
import org.netcracker.learningcenter.h2.administrator.DatabaseAdministrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for adding/removing credentials such as jira, ftp-server, etc. (by admin) from database
 *
 * @author Mikhail Savin
 */
@RestController
@RequestMapping("/administrator")
public class DatabaseAdministratorController {

    /**
     * DatabaseAdministrator instance. It is used to perform operations of DB
     */
    private final DatabaseAdministrator databaseAdministrator;

    /**
     * Creates a DatabaseAdministratorController instance
     */
    @Autowired
    public DatabaseAdministratorController(DatabaseAdministrator databaseAdministrator) {
        this.databaseAdministrator = databaseAdministrator;
    }

    /**
     * Adds, removes or updates credentials in the database
     *
     * @param jsonNode JSON with required fields
     */
    @PostMapping(value = "/interactWithDatabase", consumes = "application/json", produces = "application/json")
    public void interactWithDatabase(@RequestBody JsonNode jsonNode) throws ResourceNotFoundException {
        databaseAdministrator.doOperation(jsonNode);
    }

    /**
     * Gets all existing credentials from the database as Dto objects
     * @return JsonNode with credentials
     */
    @GetMapping(value = "/getAllExistingCredentials", consumes = "application/json", produces = "application/json")
    public JsonNode getAllExistingCredentials() {
        return databaseAdministrator.getAllAsDto();
    }
}
