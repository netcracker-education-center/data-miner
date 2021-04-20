package org.netcracker.learningcenter.h2.credentials.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class contains login credentials for the Jira account.
 *
 * @author Mikhail Savin
 */
@Entity
@Table(name = "JIRA_CREDENTIALS")
public class JiraCredentials implements Credentials {

    /**
     * Company server URL
     */
    @Id
    @Column(name = "Url", length = 64, nullable = false)
    private String url;

    /**
     * Jira account login
     */
    @Column(name = "Login", length = 64, nullable = false)
    private String login;

    /**
     * Jira account password
     */
    @Column(name = "Password", nullable = false)
    private String password;

    /**
     * @return URL of a company Jira server
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url company Jira server url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return login of the Jira account
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login login of the Jira account to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return password of the Jira account
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password password of the Jira account to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
