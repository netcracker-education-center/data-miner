package org.netcracker.learningcenter.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netcracker.learningcenter.collector.interfaces.ICollector;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Class which creates new JiraRestClient instance and gets all issues
 *
 * @author Mikhail Savin, Andranik Grigoryan
 */
public class JiraClientWorker implements ICollector {
    private static final Logger LOG = LogManager.getRootLogger();

    /**
     * Jira URL property name
     */
    private static final String JIRA_URL = "jira.url";

    /**
     * Jira login property name
     */
    private static final String JIRA_LOGIN = "jira.login";

    /**
     * Jira password property name
     */
    private static final String JIRA_PASSWORD = "jira.password";

    /**
     * JiraRestClient instance
     */
    private final JiraRestClient restClient;

    /**
     * Jira URL
     */
    private final String jiraUrl;

    /**
     * Creates a JiraClientWorker instance
     *
     * @param login Jira login
     * @param password Jira password
     * @param jiraUrl Jira url
     */
    public JiraClientWorker(String login, String password, String jiraUrl) {
        this.jiraUrl = jiraUrl;
        this.restClient = getAuthorizedClient(jiraUrl, login, password);
    }

    /**
     * Creates web link to the Jira-issue
     *
     * @param url the server url
     * @param issueKey issue key
     * @return link to the issue
     */
    private String createWebLink(String url, String issueKey) {
        return url + "/browse/" + issueKey;
    }

    /**
     * @return list of SimpleIssues from client's project
     */
    @Override
    public List<SimpleIssue> getIssues() {
        List<BasicProject> projects = new ArrayList<>();
        List<SimpleIssue> issues = new ArrayList<>();

        for (BasicProject project : restClient.getProjectClient().getAllProjects().claim()) {
            projects.add(project);
        }
        projects.forEach(p -> {
            LOG.debug("Found project: {}", p);

            Promise<SearchResult> searchJqlPromise;
            searchJqlPromise = restClient.getSearchClient().searchJql("project = " + p.getKey());

            for (Issue issue : searchJqlPromise.claim().getIssues()) {
                List<String> comments = new ArrayList<>();

                for (Comment comment : getAllComments(issue)) {
                    comments.add(comment.getBody());
                }
                issues.add(new SimpleIssue(issue.getKey(), createWebLink(jiraUrl, issue.getKey()),
                        issue.getSummary(), issue.getDescription(), comments));
            }
        });
        return issues;
    }

    /**
     * Gets Jira-issues by keywords in JQL (optionally, it is filtered by date and status)
     *
     * @param keywords Jira-issues text field keywords
     * @param issuesDate user defined latest Jira-issue date
     * @param issuesStatus user defined Jira-issue status
     * @return relevant Jira-issues as a List
     */
    public List<SimpleIssue> getIssuesByKeywords(List<String> keywords, String issuesDate, String issuesStatus) {
        List<BasicProject> projects = new ArrayList<>();
        List<SimpleIssue> issues = new ArrayList<>();

        for (BasicProject project : restClient.getProjectClient().getAllProjects().claim()) {
            projects.add(project);
        }

        projects.forEach(p -> {
            List<String> addedKeys = new ArrayList<>();
            for (String keyword : keywords) {
                String jql = "text ~ " + "\"" + keyword + "\"";
                if (!issuesDate.isEmpty()) {
                    jql += " AND created >= " + issuesDate;
                }
                if (!issuesStatus.isEmpty()) {
                    jql += " AND status=" + issuesStatus;
                }
                LOG.debug("JQL: {}", jql);

                Promise<SearchResult> searchResult = restClient.getSearchClient().searchJql(jql);
                SearchResult results = searchResult.claim();

                for (Issue issue : results.getIssues()) {
                    List<String> comments = new ArrayList<>();

                    for (Comment comment : getAllComments(issue)) {
                        comments.add(comment.getBody());
                    }

                    SimpleIssue relevantIssue = new SimpleIssue(issue.getKey(), createWebLink(jiraUrl, issue.getKey()),
                            issue.getSummary(), issue.getDescription(), comments);
                    if (!addedKeys.contains(relevantIssue.getIssueKey())) {
                        issues.add(relevantIssue);
                        addedKeys.add(relevantIssue.getIssueKey());
                    }
                }
            }
        });
        return issues;
    }

    /**
     * Gets Jira-issues based on user-specified JQL
     *
     * @param jql user's JQL to search Jira-issues
     * @return relevant Jira-issues as a List
     */
    public List<SimpleIssue> getIssuesByJql(String jql) {
        List<BasicProject> projects = new ArrayList<>();
        List<SimpleIssue> issues = new ArrayList<>();

        for (BasicProject project : restClient.getProjectClient().getAllProjects().claim()) {
            projects.add(project);
        }

        projects.forEach(p -> {
            Promise<SearchResult> searchResult = restClient.getSearchClient().searchJql(jql);
            SearchResult results = searchResult.claim();

            for (Issue issue : results.getIssues()) {
                List<String> comments = new ArrayList<>();

                for (Comment comment : getAllComments(issue)) {
                    comments.add(comment.getBody());
                }

                issues.add(new SimpleIssue(issue.getKey(), createWebLink(jiraUrl, issue.getKey()), issue.getSummary(),
                        issue.getDescription(), comments));
            }
        });
        return issues;
    }

    /**
     * @param uri      the server Uri
     * @param login    client's login. It just be any mail address
     * @param password the Jira rest Token which create user in his profile
     * @return JiraRestClient instance for working with ticket system
     */
    private JiraRestClient getAuthorizedClient(String uri, String login, String password) {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(uri), login, password);
    }

    /**
     * Authorize new client
     *
     * @param uriAddress the server Uri
     * @param login client's login. It just be any mail address
     * @param password the Jira rest Token which create user in his profile
     * @return true if authorization is successful
     */
    @Override
    public boolean authorize(String uriAddress, String login, String password) {
        boolean check = false;
        JiraRestClient restClient;
        try {
            restClient = new AsynchronousJiraRestClientFactory()
                    .createWithBasicHttpAuthentication(URI.create(uriAddress), login, password);
            if (!restClient.getProjectClient().getAllProjects().claim().toString().equals("[]"))
                check = true;
        } catch (Exception e) {
            LOG.error("Something went wrong while authorizing into Jira", e);
            return check;
        }

        return check;
    }

    /**
     * @param issue Issue instance
     * @return all comments from issue
     */
    private List<Comment> getAllComments(Issue issue) {
        return StreamSupport.stream(restClient.getIssueClient().getIssue(issue.getKey()).claim().getComments().spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * @return new URI instance
     */
    private URI getJiraUri() {
        return URI.create(this.jiraUrl);
    }

    /**
     * @return this jira rest client
     */
    public JiraRestClient getRestClient() {
        return restClient;
    }
}