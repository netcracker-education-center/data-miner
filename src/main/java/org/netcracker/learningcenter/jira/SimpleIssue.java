package org.netcracker.learningcenter.jira;

import java.time.LocalDate;
import java.util.List;

/**
 * This class represents simple issue model
 *
 * @author Mikhail Savin, Andranik Grigoryan
 */
public class SimpleIssue {

    /**
     * Key of the current issue
     */
    String issueKey;

    /**
     * Link to the issue
     */
    String issueWebLink;

    /**
     * Title of the Jira-issue
     */
    String title;

    /**
     * Issue body
     */
    String body;

    /**
     * All comments, related to the issue
     */
    List<String> comments;

    /**
     * Modification date of the issue
     */
    LocalDate modificationDate;

    /**
     * Creates a new SimpleIssue object with given issue key, issue web link, title of the issue, its body, comments and
     * modification date.
     *
     * @param issueKey key of the issue
     * @param issueWebLink web link to the Jira-issue
     * @param title title of issue
     * @param body body of the issue
     * @param comments Jira-issue comments
     * @param modificationDate modification date of the issue
     */
    public SimpleIssue(String issueKey, String issueWebLink, String title, String body, List<String> comments,
                       LocalDate modificationDate) {
        this.issueKey = issueKey;
        this.issueWebLink = issueWebLink;
        this.title = title;
        this.body = body;
        this.comments = comments;
        this.modificationDate = modificationDate;
    }

    /**
     * @return issue key
     */
    public String getIssueKey() {
        return issueKey;
    }

    /**
     * @param issueKey issue key to set
     */
    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    /**
     * @return web link of the issue
     */
    public String getIssueWebLink() {
        return issueWebLink;
    }

    /**
     * @param issueWebLink web link of the issue to set
     */
    public void setIssueWebLink(String issueWebLink) {
        this.issueWebLink = issueWebLink;
    }

    /**
     * @return title of the issue
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title title of the issue to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return body of the issue
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body body of the issue to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return comments of the issue
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * @param comments comments of the issue to set
     */
    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    /**
     * @return modification date of the issue
     */
    public LocalDate getModificationDate() {
        return modificationDate;
    }

    /**
     * @param modificationDate modification date of the issue to set
     */
    public void setModificationDate(LocalDate modificationDate) {
        this.modificationDate = modificationDate;
    }
}
