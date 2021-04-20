package org.netcracker.learningcenter.h2.credentials.entities;

import javax.persistence.*;

/**
 * This class contains login credentials for the FTP-server.
 *
 * @author Mikhail Savin
 */
@Entity
@Table(name = "FTP_SERVER_CREDENTIALS")
public class FtpServerCredentials implements Credentials {

    /**
     * Entity id
     */
    @Id
    @GeneratedValue
    @Column(name = "Id", nullable = false)
    private Long id;

    /**
     * IP address of the FTP-server
     */
    @Column(name = "Server", length = 64, nullable = false)
    private String server;

    /**
     * Port of the FTP-server
     */
    @Column(name = "Port", nullable = false)
    private int port;

    /**
     * FTP-server user login
     */
    @Column(name = "Login", length = 64, nullable = false)
    private String login;

    /**
     * FTP-server user password
     */
    @Column(name = "Password", nullable = false)
    private String password;

    /**
     * @return id of this entity
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id this entity's id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return port of the FTP-server
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port FTP-server port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return IP address of the FTP-server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server IP address of the FTP-server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return FTP-server user login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login FTP-server user login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return FTP-server user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password FTP-server user password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
