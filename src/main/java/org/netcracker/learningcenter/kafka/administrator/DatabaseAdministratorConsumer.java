package org.netcracker.learningcenter.kafka.administrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netcracker.learningcenter.enums.DatabaseOperation;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;
import org.netcracker.learningcenter.h2.administrator.DatabaseAdministrator;
import org.netcracker.learningcenter.utils.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Locale;

/**
 * Kafka-consumer class for DatabaseAdministrator service
 *
 * @author Mikhail Savin
 */
@EnableKafka
@Component
@PropertySource("/application.properties")
public class DatabaseAdministratorConsumer {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Topic with results from DataMiner
     */
    @Value("${kafka.data-miner.admin.select}")
    private String resultsTopic;

    /**
     * Type of the operation (e.g. "add", "remove", "getAll")
     */
    private static final String TYPE = "type";

    /**
     * DatabaseAdministrator instance. It is used to perform operations with database
     */
    private final DatabaseAdministrator databaseAdministrator;

    /**
     * ObjectMapper instance. Used for JsonNode mapping
     */
    private final ObjectMapper objectMapper;

    /**
     * DataMiner producer Kafka template
     */
    private final KafkaTemplate<String, JsonNode> producerKafkaTemplate;

    /**
     * Creates a DatabaseAdministratorConsumer instance
     *
     * @param databaseAdministrator current DatabaseAdministrator instance
     * @param objectMapper current ObjectMapper instance
     * @param producerKafkaTemplate current KafkaTemplate instance
     */
    @Autowired
    public DatabaseAdministratorConsumer(DatabaseAdministrator databaseAdministrator, ObjectMapper objectMapper,
                                         KafkaTemplate<String, JsonNode> producerKafkaTemplate) {
        this.databaseAdministrator = databaseAdministrator;
        this.objectMapper = objectMapper;
        this.producerKafkaTemplate = producerKafkaTemplate;
    }

    /**
     * Adds, removes, updates credentials in the database OR gets all existing credentials as CredentialDto and sends
     * them as Kafka message to the relevant topic
     *
     * @param record a record with a message
     * @throws JsonProcessingException if something bad encountered when processing (parsing, generating) JSON content,
     * that are not pure I/O problems
     */
    public void interactWithDatabase(ConsumerRecord<String, String> record) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(record.value());
        JsonNode type = jsonNode.path(TYPE);

        try {
            Validations.checkJsonNode(type);
            if (type.asText().equals(DatabaseOperation.GET_ALL.name().toLowerCase(Locale.ROOT))) {
                JsonNode credentialsAsDto = databaseAdministrator.getAllAsDto();
                ListenableFuture<SendResult<String, JsonNode>> userFuture =
                        producerKafkaTemplate.send(resultsTopic, credentialsAsDto);
                userFuture.addCallback(new ListenableFutureCallback<SendResult<String, JsonNode>>() {
                    @Override
                    public void onSuccess(SendResult<String, JsonNode> stringReportSendResult) {
                        LOG.info("Sent message=[{}] with offset=[{}]", credentialsAsDto.toString(), stringReportSendResult.getRecordMetadata().offset());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOG.error("Unable to send message=[{}] due to : {}", credentialsAsDto.toString(), throwable.getMessage());
                    }
                });
            } else {
                databaseAdministrator.doOperation(jsonNode);
                JsonNode credentialsAsDto = databaseAdministrator.getAllAsDto();
                ListenableFuture<SendResult<String, JsonNode>> userFuture =
                        producerKafkaTemplate.send(resultsTopic, credentialsAsDto);
                userFuture.addCallback(new ListenableFutureCallback<SendResult<String, JsonNode>>() {
                    @Override
                    public void onSuccess(SendResult<String, JsonNode> stringReportSendResult) {
                        LOG.info("Sent message=[{}] with offset=[{}]", credentialsAsDto.toString(), stringReportSendResult.getRecordMetadata().offset());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOG.error("Unable to send message=[{}] due to : {}", credentialsAsDto.toString(), throwable.getMessage());
                    }
                });
            }
        } catch (ResourceNotFoundException e) {
            LOG.error("JSON mandatory field is missing", e);
        }
    }
}
