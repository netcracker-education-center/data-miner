package org.netcracker.learningcenter.collector.interfaces;

import org.netcracker.learningcenter.jira.SimpleIssue;

import java.util.List;

/**
 * Interface for abstract working with ticket systems
 */
public interface ICollector {
    public boolean authorize(String uri, String login, String password);
    public List<SimpleIssue> getIssues();

}
