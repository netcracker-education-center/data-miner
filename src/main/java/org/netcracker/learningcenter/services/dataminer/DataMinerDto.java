package org.netcracker.learningcenter.services.dataminer;

import java.util.List;

/**
 * This class represents DataMiner message model (used in the Kafka producer)
 *
 * @author Mikhail Savin
 */
public class DataMinerDto {

    /**
     * Id of the user
     */
    private String userId;

    /**
     * Id of the request
     */
    private String requestId;

    /**
     * Keywords used to select relevant Jira-issues. Also used for text analysis
     */
    private List<String> keywordsList;

    /**
     * Creates a new DataMinerDto instance with the given user id, request id and keywords
     *
     * @param userId current user id
     * @param requestId current request id
     * @param keywordsList keywords used to select the desired (relevant) Jira-issues
     */
    public DataMinerDto(String userId, String requestId, List<String> keywordsList) {
        this.userId = userId;
        this.requestId = requestId;
        this.keywordsList = keywordsList;
    }

    /**
     * @return this user's id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId user id to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return this request's id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId request id to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return user keywords
     */
    public List<String> getKeywordsList() {
        return keywordsList;
    }

    /**
     * @param keywordsList user keywords to set
     */
    public void setKeywordsList(List<String> keywordsList) {
        this.keywordsList = keywordsList;
    }
}
