package org.netcracker.learningcenter.services.dataminer;

import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netcracker.educationcenter.elasticsearch.connection.Connection;
import org.netcracker.educationcenter.elasticsearch.database.model.FTPFileObject;
import org.netcracker.educationcenter.elasticsearch.database.model.JiraIssue;
import org.netcracker.educationcenter.elasticsearch.database.operations.ElasticsearchOperations;
import org.netcracker.educationcenter.elasticsearch.database.operations.ElasticsearchOperationsException;
import org.netcracker.educationcenter.elasticsearch.database.operations.FTPFileObjectOperations;
import org.netcracker.educationcenter.elasticsearch.database.operations.JiraIssueOperations;
import org.netcracker.learningcenter.jira.JiraClientWorker;
import org.netcracker.learningcenter.jira.SimpleIssue;
import org.netcracker.learningcenter.reader.ReaderFactory;
import org.netcracker.learningcenter.utils.FTPFileData;
import org.netcracker.learningcenter.utils.FileUtils;
import org.netcracker.learningcenter.utils.FtpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Service to collect and store data from different sources
 *
 * @author Mikhail Savin
 */
@Service
@PropertySource("/application.properties")
public class DataMinerService {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Reader factory instance to get reader for a specific file
     */
    @Autowired
    private ReaderFactory readerFactory;

    /**
     * Elasticsearch connection properties
     */
    private final Properties properties;

    /**
     * Loads properties to establish a connection with Elasticsearch
     *
     * @param hostname Elasticsearch hostname
     * @param scheme the name of the ES connection scheme
     * @param port1 first Elasticsearch port number
     * @param port2 second Elasticsearch port number
     */
    @Autowired
    public DataMinerService(@Value("${eshostname}") String hostname, @Value("${scheme}") String scheme,
                            @Value("${port1}") String port1, @Value("${port2}") String port2) {
        properties = new Properties();

        properties.setProperty(DataMinerConstants.HOSTNAME_PROPERTY_NAME, hostname);
        properties.setProperty(DataMinerConstants.SCHEME_PROPERTY_NAME, scheme);
        properties.setProperty(DataMinerConstants.PORT1_PROPERTY_NAME, port1);
        properties.setProperty(DataMinerConstants.PORT2_PROPERTY_NAME, port2);
    }

    /**
     * Reads files from FTP server using FtpClient instance,
     * then adds file data to Elasticsearch database as FTPFileObject models
     *
     * @param client FtpClient instance to make a connection, and download files from server
     * @param path path to the directory on the FTP server to download files from there
     * @param filters a list of filters used to validate the file before downloading
     * @param requestNumber current request number (id)
     */
    public void addFileData(FtpClient client, String path, List<FTPFileFilter> filters, String requestNumber) {
        List<FTPFileData> fileInfo = new ArrayList<>();

        try (Connection connection = new Connection(properties)) {
            connection.makeConnection();

            try (FtpClient c = client) {
                c.open();
                File tmpDir = Files.createTempDirectory("tmpFtpStorage").toFile();
                String tmpFtpStorage = tmpDir.getAbsolutePath();
                c.downloadFiles(path, filters, tmpFtpStorage);
                List<File> list = FileUtils.listFilesForFolder(tmpFtpStorage);
                for (File f : list) {
                    FTPFileData file = new FTPFileData();
                    file.setFilename(f.getName());
                    file.setServer(c.getServer());
                    file.setText(readerFactory.getReader(f.getName()).read(f));
                    file.setModificationDate(
                            Instant.ofEpochMilli(f.lastModified())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate());
                    fileInfo.add(file);
                    f.delete();
                }
                tmpDir.delete();
            } catch (Exception e) {
                LOG.error("Something went wrong while interacting with file", e);
            }

            ElasticsearchOperations elasticsearchOperations = new FTPFileObjectOperations(connection);

            for (FTPFileData ftpFileData : fileInfo) {
                FTPFileObject ftpFileObject = new FTPFileObject(requestNumber, "FTPServer",
                        ftpFileData.getServer(), ftpFileData.getText(), ftpFileData.getModificationDate());
                try {
                    elasticsearchOperations.insert(ftpFileObject, ftpFileObject.getId());
                } catch (ElasticsearchOperationsException e) {
                    LOG.error("Something went wrong while inserting ftp file object into Elasticsearch database", e);
                }
            }
        } catch (Exception e) {
            LOG.error("Something went wrong while connecting to Elasticsearch", e);
        }
    }

    /**
     * Gets relevant Jira-issues, then adds them to Elasticsearch database as JiraIssue models
     *
     * @param login login of the Jira account
     * @param password password of the Jira account
     * @param jiraUrl jira url
     * @param keywords keywords used to select the desired (relevant) Jira-issues
     * @param issuesDate latest Jira-issue date
     * @param issuesStatus relevant Jira-issue status
     * @param requestNumber current request number (id)
     */
    public void addJiraIssuesUsingKeywords(String login, String password, String jiraUrl, List<String> keywords,
                                           String issuesDate, String issuesStatus, String requestNumber) {
        List<SimpleIssue> simpleIssues = new JiraClientWorker(login, password, jiraUrl)
                .getIssuesByKeywords(keywords, issuesDate, issuesStatus);

        try (Connection connection = new Connection(properties)) {
            connection.makeConnection();

            ElasticsearchOperations elasticsearchOperations = new JiraIssueOperations(connection);

            for (SimpleIssue simpleIssue : simpleIssues) {
                JiraIssue jiraIssue = new JiraIssue(requestNumber, simpleIssue.getIssueWebLink(),
                        simpleIssue.getTitle(), simpleIssue.getBody(), simpleIssue.getComments());
                elasticsearchOperations.insert(jiraIssue, jiraIssue.getId());
            }
        } catch (ElasticsearchOperationsException e) {
            LOG.error("Something went wrong while inserting jira issue into Elasticsearch database", e);
        } catch (Exception e) {
            LOG.error("Something went wrong while connecting to Elasticsearch", e);
        }
    }

    /**
     * Gets relevant Jira-issues, then adds them to Elasticsearch database as JiraIssue models
     *
     * @param login login of the Jira account
     * @param password password of the Jira account
     * @param jiraUrl jira url
     * @param jql user defined JQL to search Jira-issues
     * @param requestNumber current request number (id)
     */
    public void addJiraIssuesUsingJql(String login, String password, String jiraUrl, String jql, String requestNumber) {
        List<SimpleIssue> simpleIssues = new JiraClientWorker(login, password, jiraUrl).getIssuesByJql(jql);

        try (Connection connection = new Connection(properties)) {
            connection.makeConnection();

            ElasticsearchOperations elasticsearchOperations = new JiraIssueOperations(connection);

            for (SimpleIssue simpleIssue : simpleIssues) {
                JiraIssue jiraIssue = new JiraIssue(requestNumber, simpleIssue.getIssueWebLink(),
                        simpleIssue.getTitle(), simpleIssue.getBody(), simpleIssue.getComments());
                elasticsearchOperations.insert(jiraIssue, jiraIssue.getId());
            }
        } catch (ElasticsearchOperationsException e) {
            LOG.error("Something went wrong while inserting jira issue into Elasticsearch database", e);
        } catch (Exception e) {
            LOG.error("Something went wrong while connecting to Elasticsearch", e);
        }
    }
}
