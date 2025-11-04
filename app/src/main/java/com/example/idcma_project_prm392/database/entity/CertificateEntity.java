package com.example.idcma_project_prm392.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.idcma_project_prm392.database.Converters;
import java.util.List;

/**
 * Entity class cho Certificate trong Room Database
 */
@Entity(tableName = "certificates")
@TypeConverters(Converters.class)
public class CertificateEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "issuer")
    private String issuer;

    @ColumnInfo(name = "credential_id")
    private String credentialId;

    @ColumnInfo(name = "issue_date")
    private String issueDate; // format dd/MM/yyyy

    @ColumnInfo(name = "expiry_date")
    private String expiryDate; // format dd/MM/yyyy

    @ColumnInfo(name = "file_path")
    private String filePath; // Local file path thay vì Firebase Storage URL

    @ColumnInfo(name = "is_archived")
    private boolean isArchived;

    @ColumnInfo(name = "tags")
    private List<String> tags;

    public CertificateEntity() {}

    public CertificateEntity(String userId, String name, String issuer, String credentialId,
                           String issueDate, String expiryDate, String filePath,
                           boolean isArchived, List<String> tags) {
        this.userId = userId;
        this.name = name;
        this.issuer = issuer;
        this.credentialId = credentialId;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.filePath = filePath;
        this.isArchived = isArchived;
        this.tags = tags;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}

