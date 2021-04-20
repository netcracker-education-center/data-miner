package org.netcracker.learningcenter.services.dataminer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.jasypt.util.text.AES256TextEncryptor;
import org.netcracker.learningcenter.enums.Source;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;
import org.netcracker.learningcenter.filter.DateFilter;
import org.netcracker.learningcenter.filter.ExtensionFilter;
import org.netcracker.learningcenter.h2.credentials.entities.ConfluenceCredentials;
import org.netcracker.learningcenter.h2.credentials.entities.FtpServerCredentials;
import org.netcracker.learningcenter.h2.credentials.entities.JiraCredentials;
import org.netcracker.learningcenter.h2.credentials.operations.ConfluenceOperations;
import org.netcracker.learningcenter.h2.credentials.operations.FtpServerOperations;
import org.netcracker.learningcenter.h2.credentials.operations.JiraOperations;
import org.netcracker.learningcenter.utils.FtpClient;
import org.netcracker.learningcenter.utils.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * This class represents Data miner which searches and collects relevant data by request
 *
 * @author Mikhail Savin
 */
@Component
public class DataMiner {

    /**
     * Current user id
     */
    private static final String USER_ID = "userId";

    /**
     * User-selected sources
     */
    private static final String SOURCES = "selectedSources";

    /**
     * Provided source
     */
    private static final String SOURCE = "source";

    /**
     * Provided credential id
     */
    private static final String CREDENTIAL_ID = "id";

    /**
     * Keywords for text analysis, also Jira-issues text field keywords
     */
    private static final String KEYWORDS = "keywords";

    /**
     * User defined JQL
     */
    private static final String JQL = "/jiraIssues/jql";

    /**
     * User defined latest Jira-issue date
     */
    private static final String JIRA_ISSUES_DATE = "/jiraIssues/jiraIssuesDate";

    /**
     * User defined Jira-issue status
     */
    private static final String JIRA_ISSUES_STATUS = "/jiraIssues/jiraIssuesStatus";

    /**
     * Path to the directory on the FTP server from which to download files
     */
    private static final String PATH_TO_DIR = "/ftpFiles/pathToDir";

    /**
     * File filter by date
     */
    private static final String DATE_FILTER = "/ftpFiles/dateFilter";

    /**
     * File filter by extension
     */
    private static final String EXTENSION_FILTER = "/ftpFiles/extensionFilter";

    /**
     * User defined CQL
     */
    private static final String CQL = "/confPages/cql";

    /**
     * User defined latest Confluence-page date
     */
    private static final String CONFLUENCE_PAGES_DATE = "/confPages/confPagesDate";

    /**
     * DataMiner service instance to collect and store data
     */
    private final DataMinerService dataMinerService;

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
     * AES256TextEncryptor instance to encrypt passwords
     */
    private final AES256TextEncryptor aes256TextEncryptor;

    /**
     * List of keywords
     */
    List<String> keywordsList;

    /**
     * Creates a DataMiner instance
     *
     * @param dataMinerService     current DataMinerService instance
     * @param objectMapper         current ObjectMapper instance
     * @param jiraOperations       current JiraOperations instance
     * @param ftpServerOperations  current FtpServerOperations instance
     * @param confluenceOperations current ConfluenceOperations instance
     * @param aes256TextEncryptor  current AES256TextEncryptor instance
     */
    @Autowired
    public DataMiner(DataMinerService dataMinerService, ObjectMapper objectMapper, JiraOperations jiraOperations,
                     FtpServerOperations ftpServerOperations, ConfluenceOperations confluenceOperations,
                     AES256TextEncryptor aes256TextEncryptor) {
        this.dataMinerService = dataMinerService;
        this.objectMapper = objectMapper;
        this.jiraOperations = jiraOperations;
        this.ftpServerOperations = ftpServerOperations;
        this.confluenceOperations = confluenceOperations;
        this.aes256TextEncryptor = aes256TextEncryptor;
    }

    /**
     * Collects Jira Issues from all provided sources
     *
     * @param jsonNode        JSON with required fields
     * @param requestId       id of the current request
     * @param jiraCredentials list of Jira credentials to be able to collect data by their url
     * @throws ResourceNotFoundException if JsonNode mandatory field is missing
     */
    private void collectJiraIssues(JsonNode jsonNode, String requestId, List<JiraCredentials> jiraCredentials)
            throws ResourceNotFoundException {
        JsonNode jiraIssuesDate = jsonNode.at(JIRA_ISSUES_DATE);
        JsonNode jiraIssuesStatus = jsonNode.at(JIRA_ISSUES_STATUS);
        JsonNode jql = jsonNode.at(JQL);

        Validations.checkJsonNode(jiraIssuesDate, jiraIssuesStatus, jql);

        for (JiraCredentials credentials : jiraCredentials) {
            String jiraLogin = credentials.getLogin();
            String jiraPassword = aes256TextEncryptor.decrypt(credentials.getPassword());
            String jiraUrl = credentials.getUrl();

            if (!jql.isEmpty()) {
                dataMinerService.addJiraIssuesUsingJql(jiraLogin, jiraPassword, jiraUrl,
                        jql.asText(), requestId);
            } else if (!jsonNode.path(KEYWORDS).isEmpty()) {
                dataMinerService.addJiraIssuesUsingKeywords(jiraLogin, jiraPassword, jiraUrl,
                        keywordsList, jiraIssuesDate.asText(), jiraIssuesStatus.asText(), requestId);
            }
        }
    }

    /**
     * Collects FTP-server file objects from all provided sources
     *
     * @param jsonNode             JSON with required fields
     * @param requestId            id of the current request
     * @param ftpServerCredentials list of FTP-server credentials to be able to collect data by their ip addresses
     * @throws ResourceNotFoundException if JsonNode mandatory field is missing
     */
    private void collectFtpFileObjects(JsonNode jsonNode, String requestId,
                                       List<FtpServerCredentials> ftpServerCredentials)
            throws ResourceNotFoundException {
        JsonNode jPath = jsonNode.at(PATH_TO_DIR);
        JsonNode jDateFilter = jsonNode.at(DATE_FILTER);
        JsonNode jExtensionFilter = jsonNode.at(EXTENSION_FILTER);

        Validations.checkJsonNode(jPath, jDateFilter, jExtensionFilter);

        for (FtpServerCredentials credentials : ftpServerCredentials) {
            String ftpServer = credentials.getServer();
            int ftpPort = credentials.getPort();
            String ftpLogin = credentials.getLogin();
            String ftpPassword = aes256TextEncryptor.decrypt(credentials.getPassword());

            FtpClient client = new FtpClient(ftpServer, ftpPort, ftpLogin, ftpPassword);

            if (!jPath.isEmpty()) {
                List<FTPFileFilter> filters = new ArrayList<>();
                if (!jDateFilter.isMissingNode()) {
                    filters.add(new DateFilter(jDateFilter.asText()));
                }
                if (!jExtensionFilter.isMissingNode()) {
                    List<String> extensions = new ArrayList<>();
                    Iterator<JsonNode> extFilter = jExtensionFilter.elements();
                    while (extFilter.hasNext()) {
                        extensions.add(extFilter.next().asText());
                    }
                    filters.add(new ExtensionFilter(extensions));
                }
                dataMinerService.addFileData(client, jPath.asText(), filters, requestId);
            }
        }
    }

    /**
     * Collects all found data from different sources(e.g. Jira, FTP server) in the ES database
     *
     * @param jsonNode JSON with required fields
     * @return JsonNode with id of the request and keywords (used in the analysis-service)
     * @throws ResourceNotFoundException if JsonNode mandatory field is missing
     */
    public JsonNode searchAndCollect(JsonNode jsonNode) throws ResourceNotFoundException {
        String requestId = UUID.randomUUID().toString();

        JsonNode keywords = jsonNode.path(KEYWORDS);
        JsonNode userId = jsonNode.path(USER_ID);
        JsonNode sources = jsonNode.path(SOURCES);

        Validations.checkJsonNode(keywords, userId, sources);

        keywordsList = new ArrayList<>();
        for (JsonNode keyword : keywords) {
            keywordsList.add(keyword.asText());
        }

        List<JiraCredentials> jiraCredentials = new ArrayList<>();
        List<FtpServerCredentials> ftpServerCredentials = new ArrayList<>();
        List<ConfluenceCredentials> confluenceCredentials = new ArrayList<>();

        if (sources.isArray()) {
            for (JsonNode arrayItem : sources) {
                if (arrayItem.get(SOURCE).asText().equals(Source.JIRA.name())) {
                    jiraCredentials.add(jiraOperations.getJiraCredentialsByUrl(arrayItem.get(CREDENTIAL_ID).asText()));
                } else if (arrayItem.get(SOURCE).asText().equals(Source.FTP.name())) {
                    ftpServerCredentials.addAll(ftpServerOperations.getFtpServerCredentialsByServer(arrayItem.get(CREDENTIAL_ID).asText()));
                } else if (arrayItem.get(SOURCE).asText().equals(Source.CONFLUENCE.name())) {
                    confluenceCredentials.add(confluenceOperations.getConfluenceCredentialsByUrl(arrayItem.get(CREDENTIAL_ID).asText()));
                }
            }
        }

        if (!jiraCredentials.isEmpty()) {
            collectJiraIssues(jsonNode, requestId, jiraCredentials);
        }
        if (!ftpServerCredentials.isEmpty()) {
            collectFtpFileObjects(jsonNode, requestId, ftpServerCredentials);
        }
        return objectMapper.valueToTree(new DataMinerDto(userId.asText(), requestId, keywordsList));
    }
}