package org.netcracker.learningcenter.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;
import org.netcracker.learningcenter.services.dataminer.DataMiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for testing DataMiner's Kafka producer message generating
 *
 * @author Mikhail Savin
 */
@RestController
@RequestMapping("/kafka-data-miner")
public class DataMinerKafkaController {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Topic with results from DataMiner
     */
    @Value("kafka.data-miner.results")
    private String resultsTopic;

    /**
     * DataMiner instance. It is used to search and collect data
     */
    private final DataMiner dataMiner;

    /**
     * Creates a DataMinerController instance
     */
    @Autowired
    public DataMinerKafkaController(DataMiner dataMiner) {
        this.dataMiner = dataMiner;
    }

    /**
     * KafkaTemplate for generating message containing DataMinerDto object to send it to analysis-service
     */
    @Autowired
    private KafkaTemplate<String, JsonNode> kafkaTemplate;

    /**
     * Initializes data search and collection. Sends message to Kafka topic "collecting.data-miner.results"
     *
     * @param jsonNode JSON with required fields
     * @throws ResourceNotFoundException if there is no required JsonNode field
     */
    @PostMapping(value = "/message", consumes = "application/json", produces = "application/json")
    public JsonNode sendMessage(JsonNode jsonNode) throws ResourceNotFoundException {
        JsonNode dataMinerDtoAsJsonNode = dataMiner.searchAndCollect(jsonNode);
        ListenableFuture<SendResult<String, JsonNode>> userFuture =
                kafkaTemplate.send(resultsTopic, dataMinerDtoAsJsonNode);
        userFuture.addCallback(new ListenableFutureCallback<SendResult<String, JsonNode>>() {
            @Override
            public void onSuccess(SendResult<String, JsonNode> stringReportSendResult) {
                LOG.info("Sent message=[{}] with offset=[{}]", dataMinerDtoAsJsonNode.toString(), stringReportSendResult.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOG.error("Unable to send message=[{}] due to : {}", dataMinerDtoAsJsonNode.toString(), throwable.getMessage());
            }
        });
        return dataMinerDtoAsJsonNode;
    }
}
