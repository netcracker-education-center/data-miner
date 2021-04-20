package org.netcracker.learningcenter.confluence;

import java.time.LocalDate;
import java.util.List;

public class ConfluencePageModel {
    private String title;
    private String webLink;
    private String body;
    private List<String> comments;
    private LocalDate lastUpdated;

    public ConfluencePageModel() {
    }

    public ConfluencePageModel(String title, String webLink, String body, List<String> comments,LocalDate lastUpdated) {
        this.title = title;
        this.webLink = webLink;
        this.body = body;
        this.comments = comments;
        this.lastUpdated = lastUpdated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getComments() {
        return comments;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "ConfluencePageModel{" +
                "title='" + title + '\'' +
                ", webLink='" + webLink + '\'' +
                ", body='" + body + '\'' +
                ", comments=" + comments +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
