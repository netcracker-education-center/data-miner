package org.netcracker.learningcenter.h2.credentials.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class contains login credentials for the Confluence account.
 *
 * @author Mikhail Savin
 */
@Entity
@Table(name = "CONFLUENCE_CREDENTIALS")
public class ConfluenceCredentials implements Credentials {

    /**
     * Company server URL
     */
    @Id
    @Column(name = "Url", length = 64, nullable = false)
    private String url;

    /**
     * Confluence account login token
     */
    @Column(name = "Token", length = 64, nullable = false)
    private String token;

    /**
     * @return URL of a company Confluence server
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url company Confluence server url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return Confluence account token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token Confluence account token to set
     */
    public void setToken(String token) {
        this.token = token;
    }
}
