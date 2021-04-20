package org.netcracker.learningcenter.h2.administrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netcracker.learningcenter.enums.DatabaseOperation;
import org.netcracker.learningcenter.enums.Source;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;
import org.netcracker.learningcenter.h2.credentials.entities.ConfluenceCredentials;
import org.netcracker.learningcenter.h2.credentials.entities.FtpServerCredentials;
import org.netcracker.learningcenter.h2.credentials.entities.JiraCredentials;
import org.netcracker.learningcenter.h2.credentials.operations.ConfluenceOperations;
import org.netcracker.learningcenter.h2.credentials.operations.FtpServerOperations;
import org.netcracker.learningcenter.h2.credentials.operations.JiraOperations;
import org.netcracker.learningcenter.utils.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * DatabaseAdministrator performs operations with database based on the received JsonNode
 *
 * @author Mikhail Savin
 */
@Component
public class DatabaseAdministrator {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Type of the operation (e.g. "add", "remove", "update") (JsonNode field)
     */
    private static final String TYPE = "type";

    /**
     * Provided source (e.g. "jira", "confluence", "ftp") (JsonNode field)
     */
    private static final String SOURCE = "source";

    /**
     * Jira URL of a company server (JsonNode field)
     */
    private static final String JIRA_URL = "/credentials/url";

    /**
     * Jira account login (JsonNode field)
     */
    private static final String JIRA_LOGIN = "/credentials/login";

    /**
     * Jira account password (JsonNode field)
     */
    private static final String JIRA_PASSWORD = "/credentials/password";

    /**
     * IP address of the FTP-server (JsonNode field)
     */
    private static final String FTP_SERVER = "/credentials/server";

    /**
     * Port of the FTP-server (JsonNode field)
     */
    private static final String FTP_PORT = "/credentials/port";

    /**
     * FTP-server user login (JsonNode field)
     */
    private static final String FTP_LOGIN = "/credentials/login";

    /**
     * FTP-server user password (JsonNode field)
     */
    private static final String FTP_PASSWORD = "/credentials/password";

    /**
     * Confluence URL of a company server (JsonNode field)
     */
    private static final String CONFLUENCE_URL = "/credentials/url";

    /**
     * Confluence account login token (JsonNode field)
     */
    private static final String CONFLUENCE_TOKEN = "/credentials/token";

    /**
     * JiraOperations instance to perform operations with Jira credentials on database
     */
    private final JiraOperations jiraOperations;

    /**
     * FtpServerOperations instance to perform operations with FTP-server credentials on database
     */
    private final FtpServerOperations ftpServerOperations;

    /**
     * ConfluenceOperations instance to perform operations with Confluence credentials on database
     */
    private final ConfluenceOperations confluenceOperations;

    /**
     * ObjectMapper instance. Used for JsonNode mapping
     */
    private final ObjectMapper objectMapper;

    /**
     * Creates a DatabaseAdministrator instance
     *
     * @param jiraOperations current JiraOperations instance
     * @param ftpServerOperations current FtpServerOperations instance
     * @param confluenceOperations current ConfluenceOperations instance
     * @param objectMapper current ObjectMapper instance
     */
    @Autowired
    public DatabaseAdministrator(JiraOperations jiraOperations, FtpServerOperations ftpServerOperations,
                                 ConfluenceOperations confluenceOperations, ObjectMapper objectMapper) {
        this.jiraOperations = jiraOperations;
        this.ftpServerOperations = ftpServerOperations;
        this.confluenceOperations = confluenceOperations;
        this.objectMapper = objectMapper;
    }

    /**
     * Performs the required database operation on the specified source
     *
     * @param jsonNode JSON with required fields
     * @throws ResourceNotFoundException if JsonNode mandatory field is missing
     */
    public void doOperation(JsonNode jsonNode) throws ResourceNotFoundException {
        JsonNode type = jsonNode.path(TYPE);
        JsonNode source = jsonNode.path(SOURCE);

        switch (Source.valueOf(source.asText().toUpperCase())) {
            case JIRA:
                JsonNode jiraUrl = jsonNode.at(JIRA_URL);
                JsonNode jiraLogin = jsonNode.at(JIRA_LOGIN);
                JsonNode jiraPassword = jsonNode.at(JIRA_PASSWORD);

                Validations.checkJsonNode(type, source, jiraUrl, jiraLogin, jiraPassword);

                JiraCredentials jiraCredentials = new JiraCredentials();
                jiraCredentials.setUrl(jiraUrl.asText());
                jiraCredentials.setLogin(jiraLogin.asText());
                jiraCredentials.setPassword(jiraPassword.asText());

                if (type.asText().equals(DatabaseOperation.ADD.name().toLowerCase(Locale.ROOT))) {
                    jiraOperations.add(jiraCredentials);
                } else if (type.asText().equals(DatabaseOperation.REMOVE.name().toLowerCase(Locale.ROOT))) {
                    jiraOperations.remove(jiraCredentials);
                } else if (type.asText().equals(DatabaseOperation.UPDATE.name().toLowerCase(Locale.ROOT))) {
                    jiraOperations.update(jiraCredentials);
                }
                break;
            case FTP:
                JsonNode ftpServer = jsonNode.at(FTP_SERVER);
                JsonNode ftpPort = jsonNode.at(FTP_PORT);
                JsonNode ftpLogin = jsonNode.at(FTP_LOGIN);
                JsonNode ftpPassword = jsonNode.at(FTP_PASSWORD);

                Validations.checkJsonNode(ftpLogin, ftpPassword, ftpPort, ftpServer);

                FtpServerCredentials ftpServerCredentials = new FtpServerCredentials();
                ftpServerCredentials.setServer(ftpServer.asText());
                ftpServerCredentials.setPort(ftpPort.asInt());
                ftpServerCredentials.setLogin(ftpLogin.asText());
                ftpServerCredentials.setPassword(ftpPassword.asText());

                if (type.asText().equals(DatabaseOperation.ADD.name().toLowerCase(Locale.ROOT))) {
                    ftpServerOperations.add(ftpServerCredentials);
                } else if (type.asText().equals(DatabaseOperation.REMOVE.name().toLowerCase(Locale.ROOT))) {
                    ftpServerOperations.remove(ftpServerCredentials);
                } else if (type.asText().equals(DatabaseOperation.UPDATE.name().toLowerCase(Locale.ROOT))) {
                    ftpServerOperations.update(ftpServerCredentials);
                }
                break;
            case CONFLUENCE:
                JsonNode confluenceUrl = jsonNode.at(CONFLUENCE_URL);
                JsonNode confluenceToken = jsonNode.at(CONFLUENCE_TOKEN);

                Validations.checkJsonNode(confluenceUrl, confluenceToken);

                ConfluenceCredentials confluenceCredentials = new ConfluenceCredentials();
                confluenceCredentials.setUrl(confluenceUrl.asText());
                confluenceCredentials.setToken(confluenceToken.asText());

                if (type.asText().equals(DatabaseOperation.ADD.name().toLowerCase(Locale.ROOT))) {
                    confluenceOperations.add(confluenceCredentials);
                } else if (type.asText().equals(DatabaseOperation.REMOVE.name().toLowerCase(Locale.ROOT))) {
                    confluenceOperations.remove(confluenceCredentials);
                } else if (type.asText().equals(DatabaseOperation.UPDATE.name().toLowerCase(Locale.ROOT))) {
                    confluenceOperations.update(confluenceCredentials);
                }
                break;
            default:
                LOGGER.error("No supported source");
                break;
        }
    }

    /**
     * Gets all existing credentials from database as CredentialDto instance
     *
     * @return JsonNode with the list of CredentialDto instances
     */
    public JsonNode getAllAsDto() {
        List<CredentialDto> sourceDtoList = new ArrayList<>();

        List<JiraCredentials> jiraCredentialsList = jiraOperations.getAll();
        List<FtpServerCredentials> ftpServerCredentialsList = ftpServerOperations.getAll();
        List<ConfluenceCredentials> confluenceCredentialsList = confluenceOperations.getAll();

        for (JiraCredentials credentials : jiraCredentialsList) {
            CredentialDto credentialDto = new CredentialDto(Source.JIRA.name(), credentials.getUrl());
            sourceDtoList.add(credentialDto);
        }

        for (FtpServerCredentials credentials : ftpServerCredentialsList) {
            CredentialDto credentialDto = new CredentialDto(Source.FTP.name(), credentials.getServer());
            sourceDtoList.add(credentialDto);
        }

        for (ConfluenceCredentials credentials : confluenceCredentialsList) {
            CredentialDto credentialDto = new CredentialDto(Source.CONFLUENCE.name(), credentials.getUrl());
            sourceDtoList.add(credentialDto);
        }
        return objectMapper.valueToTree(sourceDtoList);
    }
}
