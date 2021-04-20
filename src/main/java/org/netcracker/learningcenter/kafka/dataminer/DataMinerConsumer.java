package org.netcracker.learningcenter.kafka.dataminer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;
import org.netcracker.learningcenter.services.dataminer.DataMiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Kafka-consumer class for DataMiner service
 *
 * @author Mikhail Savin
 */
@EnableKafka
@Component
@PropertySource("/application.properties")
public class DataMinerConsumer {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Topic with results from DataMiner
     */
    @Value("${kafka.data-miner.results}")
    private String resultsTopic;

    /**
     * DataMiner instance. It is used to search and collect data
     */
    private final DataMiner dataMiner;

    /**
     * ObjectMapper instance. Used for JsonNode mapping
     */
    private final ObjectMapper objectMapper;

    /**
     * Creates a DataMinerConsumer instance
     *
     * @param dataMiner current DataMiner instance
     * @param objectMapper current ObjectMapper instance
     */
    @Autowired
    public DataMinerConsumer(DataMiner dataMiner, ObjectMapper objectMapper) {
        this.dataMiner = dataMiner;
        this.objectMapper = objectMapper;
    }

    /**
     * DataMiner producer Kafka template
     */
    @Autowired
    private KafkaTemplate<String, JsonNode> producerKafkaTemplate;

    /**
     * Initializes data search and collection Sends message to Kafka topic "collecting.data-miner.results"
     *
     * @param record a record with a message
     * @throws JsonProcessingException if something bad encountered when processing (parsing, generating) JSON content,
     * that are not pure I/O problems
     */
    public void searchAndCollect(ConsumerRecord<String, String> record) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(record.value());

        try {
            JsonNode dataMinerDtoAsJsonNode = dataMiner.searchAndCollect(jsonNode);
            ListenableFuture<SendResult<String, JsonNode>> userFuture =
                    producerKafkaTemplate.send(resultsTopic, dataMinerDtoAsJsonNode);
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
        } catch (ResourceNotFoundException e) {
            LOG.error("JSON mandatory field is missing", e);
        }
    }
}
