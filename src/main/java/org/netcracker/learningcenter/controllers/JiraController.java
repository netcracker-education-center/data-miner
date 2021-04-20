package org.netcracker.learningcenter.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;
import org.netcracker.learningcenter.jira.JiraClientWorker;
import org.netcracker.learningcenter.jira.SimpleIssue;
import org.netcracker.learningcenter.utils.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class to test methods that use Jira API
 *
 * @author Mikhail Savin
 */
@RequestMapping("/jira")
@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class JiraController {

    /**
     * User defined JQL
     */
    private static final String JQL = "jql";

    /**
     * User defined Jira Keywords to search for issues
     */
    private static final String JIRA_KEYWORDS = "keywords";

    /**
     * User defined latest Jira-issue date
     */
    private static final String JIRA_ISSUES_DATE = "jiraIssuesDate";

    /**
     * User defined Jira-issue status
     */
    private static final String JIRA_ISSUES_STATUS = "jiraIssuesStatus";

    /**
     * Jira login
     */
    @Value("${jira.login}")
    private String jiraLogin;

    /**
     * Jira password
     */
    @Value("${jira.password}")
    private String jiraPassword;

    /**
     * Jira url for connection
     */
    @Value("${jira.url}")
    private String jiraUrl;

    /**
     * Creates JiraController instance
     */
    @Autowired
    public JiraController() {

    }

    /**
     * This method gets all issues from actual account
     *
     * @return all account issues
     */
    @GetMapping(value = "/getAllIssues", produces = "application/json")
    public List<SimpleIssue> getIssues() {
        return new JiraClientWorker(jiraLogin, jiraPassword, jiraUrl).getIssues();
    }

    /**
     * This method gets all issues that contains keywords. Optionally, it can also filter issues
     * by date and issues status
     *
     * @param jsonNode JSON with required fields
     * @return relevant issues
     * @throws ResourceNotFoundException if there is no required JsonNode field
     */
    @PostMapping(value = "/getIssuesByKeywords", produces = "application/json", consumes = "application/json")
    public List<SimpleIssue> getIssuesByKeywords(@RequestBody JsonNode jsonNode) throws ResourceNotFoundException {
        JsonNode jiraKeywords = jsonNode.path(JIRA_KEYWORDS);
        JsonNode jiraIssuesDate = jsonNode.path(JIRA_ISSUES_DATE);
        JsonNode jiraIssuesStatus = jsonNode.path(JIRA_ISSUES_STATUS);
        Validations.checkJsonNode(jiraKeywords, jiraIssuesDate, jiraIssuesStatus);

        List<String> keywords = new ArrayList<>();

        for (JsonNode keyword : jiraKeywords) {
            keywords.add(keyword.asText());
        }

        return new JiraClientWorker(jiraLogin, jiraPassword, jiraUrl)
                .getIssuesByKeywords(keywords, jiraIssuesDate.asText(), jiraIssuesStatus.asText());
    }

    /**
     * This method gets all issues that match search conditions using user defined JQL
     *
     * @param jsonNode JSON with required fields
     * @return relevant issues
     * @throws ResourceNotFoundException if there is no required JsonNode field
     */
    @PostMapping(value = "/getIssuesByJql", produces = "application/json", consumes = "application/json")
    public List<SimpleIssue> getIssuesByJql(@RequestBody JsonNode jsonNode) throws ResourceNotFoundException {
        JsonNode jql = jsonNode.path(JQL);
        Validations.checkJsonNode(jql);

        return new JiraClientWorker(jiraLogin, jiraPassword, jiraUrl).getIssuesByJql(jql.asText());
    }
}
