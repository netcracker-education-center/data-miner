package org.netcracker.learningcenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasypt.util.text.AES256TextEncryptor;
import org.netcracker.learningcenter.confluence.ConfluenceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration class for the application
 *
 * @author Mikhail Savin, Alexander Sakharov
 */
@Configuration
public class ApplicationConfiguration {

    /**
     * Jasypt password to encrypt and decrypt data
     */
    @Value("${jasypt.password}")
    private String jasyptPassword;

    /**
     * @return object mapper for JsonNode mapping
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * @return text encryptor to encrypt and decrypt passwords or something else
     */
    @Bean
    public AES256TextEncryptor getEncryptor() {
        AES256TextEncryptor basicTextEncryptor = new AES256TextEncryptor();
        basicTextEncryptor.setPasswordCharArray(jasyptPassword.toCharArray());
        return basicTextEncryptor;
    }

    @Bean
    @Scope(value = "prototype")
    public ConfluenceClient confluenceClient(String url, String token) {
        return new ConfluenceClient(url, token, objectMapper());
    }
}
