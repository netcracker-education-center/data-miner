package org.netcracker.learningcenter.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;
import org.netcracker.learningcenter.services.dataminer.DataMiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for data detection and collection using Elasticsearch database
 *
 * @author Mikhail Savin
 */
@RestController
@RequestMapping("/data-miner")
public class DataMinerController {

    /**
     * DataMiner instance. It is used to search and collect data
     */
    private final DataMiner dataMiner;

    /**
     * Creates a DataMinerController instance
     */
    @Autowired
    public DataMinerController(DataMiner dataMiner) {
        this.dataMiner = dataMiner;
    }

    /**
     * Initializes data search and collection
     *
     * @param jsonNode JSON with required fields
     */
    @PostMapping(value = "/collect", consumes = "application/json", produces = "application/json")
    public JsonNode searchAndCollect(@RequestBody JsonNode jsonNode) throws ResourceNotFoundException {
        return dataMiner.searchAndCollect(jsonNode);
    }
}