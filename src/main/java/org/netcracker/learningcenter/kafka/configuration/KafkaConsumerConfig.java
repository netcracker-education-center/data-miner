package org.netcracker.learningcenter.kafka.configuration;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Consumer configuration class for Kafka
 *
 * @author Mikhail Savin
 */
@Configuration
public class KafkaConsumerConfig {

    /**
     * IP of the Kafka-server
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    /**
     * Consumer group id
     */
    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaGroupId;

    /**
     * Sets consumer's configuration
     *
     * @return map with consumer configuration properties
     */
    @Bean
    public Map<String, Object> kafkaConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return props;
    }

    /**
     * Kafka consumer factory. It uses consumer configurations to create a consumer
     *
     * @return default kafka factory for consumer with consumer configuration set
     */
    @Bean
    public ConsumerFactory<String, String> kafkaConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfigs());
    }

    /**
     * Kafka listener container factory
     *
     * @return factory with configured consumer
     */
    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory());
        return factory;
    }
}
