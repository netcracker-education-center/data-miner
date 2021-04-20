package org.netcracker.learningcenter;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netcracker.learningcenter.kafka.administrator.DatabaseAdministratorConsumer;
import org.netcracker.learningcenter.kafka.dataminer.DataMinerConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Main application class. Used to run app
 *
 * @author Mikhail Savin
 */
@EnableKafka
@SpringBootApplication
public class Application {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * DataMinerConsumer instance. It is used to init search after getting topic message
     */
    private final DataMinerConsumer dataMinerConsumer;

    /**
     * DatabaseAdministratorConsumer instance. It is used to perform operations with database
     */
    private final DatabaseAdministratorConsumer databaseAdministratorConsumer;

    /**
     * Creates an Application instance
     *
     * @param dataMinerConsumer DataMiner consumer instance to set
     * @param databaseAdministratorConsumer DatabaseAdministrator consumer instance to set
     */
    @Autowired
    public Application(DataMinerConsumer dataMinerConsumer,
                       DatabaseAdministratorConsumer databaseAdministratorConsumer) {
        this.dataMinerConsumer = dataMinerConsumer;
        this.databaseAdministratorConsumer = databaseAdministratorConsumer;
    }

    /**
     * This method runs Spring Application
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }

    /**
     * Listens to relevant topic to get record(message) and run Data Miner
     *
     * @param record record from ui
     */
    @KafkaListener(topics="${kafka.data-miner.request}")
    public void kafkaDataMinerListener(ConsumerRecord<String, String> record){
        try {
            dataMinerConsumer.searchAndCollect(record);
        } catch (JsonProcessingException e) {
            LOG.error("Something bad encountered when processing JSON content", e);
        }
    }

    /**
     * Listens to relevant topic to get record(message) and interacts with database (performs CRUD operations)
     *
     * @param record record from ui
     */
    @KafkaListener(topics="${kafka.data-miner.admin}")
    public void kafkaCredentialsListener(ConsumerRecord<String, String> record){
        try {
            databaseAdministratorConsumer.interactWithDatabase(record);
        } catch (JsonProcessingException e) {
            LOG.error("Something bad encountered when processing JSON content", e);
        }
    }
}
