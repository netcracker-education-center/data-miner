package org.netcracker.learningcenter.h2.administrator;

/**
 * This class represents existing credentials from database
 *
 * @author Mikhail Savin
 */
public class CredentialDto {

    /**
     * Credential source Type
     */
    private String sourceType;

    /**
     * Id of the credential
     */
    private String id;

    /**
     * Creates a new CredentialDto instance with the given sourceType and id
     *
     * @param sourceType type of the source
     * @param id current source's id (e.g.) url, ip
     */
    public CredentialDto(String sourceType, String id) {
        this.sourceType = sourceType;
        this.id = id;
    }

    /**
     * @return this credential source's type
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * @param sourceType type of the credential source to set
     */
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * @return this credential source's id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id id of the credential source to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
