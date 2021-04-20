package org.netcracker.learningcenter.jira;

import java.util.List;

public class SimpleIssue {
    String issueKey;
    String issueWebLink;
    String title;
    String body;
    List<String> comments;

    public SimpleIssue(String issueKey, String issueWebLink, String title, String body, List<String> comments) {
        this.issueKey = issueKey;
        this.issueWebLink = issueWebLink;
        this.title = title;
        this.body = body;
        this.comments = comments;
    }

    public List<String> getComments() {
        return comments;
    }

    public String getTitle() {
        return title;
    }
    public String getBody() {
        return body;
    }
    public String getIssueKey() {
        return issueKey;
    }
    public String getIssueWebLink() {
        return issueWebLink;
    }
}
