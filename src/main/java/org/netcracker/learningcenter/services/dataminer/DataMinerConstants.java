package org.netcracker.learningcenter.services.dataminer;

/**
 * This class stores constants for classes in the DataMiner package
 *
 * @author Mikhail Savin
 */
public final class DataMinerConstants {

    /**
     * Elasticsearch hostname property name
     */
    public static final String HOSTNAME_PROPERTY_NAME = "hostname";

    /**
     * Elasticsearch scheme property name
     */
    public static final String SCHEME_PROPERTY_NAME = "scheme";

    /**
     * Elasticsearch first port property name.
     */
    public static final String PORT1_PROPERTY_NAME = "port1";

    /**
     * Elasticsearch second port property name.
     */
    public static final String PORT2_PROPERTY_NAME = "port2";

    /**
     * Elasticsearch document index for Jira-issues
     */
    public static final String JIRA_INDEX = "jiraissues";

    /**
     * Elasticsearch document index for FTP-server files
     */
    public static final String FTP_INDEX = "ftpfileobjects";

    /**
     * Elasticsearch document index for Confluence-pages
     */
    public static final String CONFLUENCE_INDEX = "confluencepages";
}
