package org.netcracker.learningcenter.kafka.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Producer configuration class for Kafka
 *
 * @author Mikhail Savin
 */
@Configuration
public class KafkaProducerConfig {

    /**
     * IP of the Kafka-server
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    /**
     * Producer group id
     */
    @Value("${spring.kafka.producer.group-id}")
    private String kafkaGroupId;

    /**
     * Sets producer's configuration
     *
     * @return map with producer configuration properties
     */
    @Bean
    public Map<String, Object> kafkaProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaGroupId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    /**
     * Kafka producer factory. It uses producer configurations to create a producer
     *
     * @return default kafka factory for producer with producer configuration set
     */
    @Bean
    public ProducerFactory<String, JsonNode> kafkaProducerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfigs());
    }

    /**
     * Producer Kafka template
     *
     * @return template with configured producer and message converter
     */
    @Bean
    public KafkaTemplate<String, JsonNode> kafkaTemplate() {
        KafkaTemplate<String, JsonNode> template = new KafkaTemplate<>(kafkaProducerFactory());
        template.setMessageConverter(new JsonMessageConverter());
        return template;
    }
}
