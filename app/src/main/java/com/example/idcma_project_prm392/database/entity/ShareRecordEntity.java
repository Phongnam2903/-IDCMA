package com.example.idcma_project_prm392.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Entity class cho Share Record trong Room Database
 */
@Entity(tableName = "share_record")
@Entity(tableName = "share_records")
public class ShareRecordEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "certificateId")
    private long certificateId;
    @ColumnInfo(name = "certificate_id")
    private String certificateId;

    @ColumnInfo(name = "user_id")
    private long userId;          // owner who shared
    private String userId; // owner who shared

    @ColumnInfo(name = "recipient_email")
    private String recipientEmail;  // optional

    @ColumnInfo(name = "share_date")
    private String shareDate;       // dd/MM/yyyy HH:mm

    @ColumnInfo(name = "link")
    private String link;            // secure link
    @ColumnInfo(name = "share_token")
    private String shareToken; // unique token for secure link

    @ColumnInfo(name = "expired")
    private boolean expired;
    @ColumnInfo(name = "expiration_date")
    private String expirationDate; // dd/MM/yyyy HH:mm

    @ColumnInfo(name = "is_expired")
    private boolean isExpired;

    @ColumnInfo(name = "status")
    private String status;          // e.g. "Active", "Expired", "Revoked"
    private String status; // "Active", "Expired", "Revoked"

    public ShareRecordEntity() {}

    @Ignore
    public ShareRecordEntity(long certificateId, long userId, String recipientEmail,
                             String shareDate, String link, boolean expired, String status) {
    public ShareRecordEntity(String certificateId, String userId, String recipientEmail,
                           String shareDate, String shareToken, String expirationDate,
                           boolean isExpired, String status) {
        this.certificateId = certificateId;
        this.userId = userId;
        this.recipientEmail = recipientEmail;
        this.shareDate = shareDate;
        this.link = link;
        this.expired = expired;
        this.shareToken = shareToken;
        this.expirationDate = expirationDate;
        this.isExpired = isExpired;
        this.status = status;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCertificateId() { return certificateId; }
    public void setCertificateId(long certificateId) { this.certificateId = certificateId; }
    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getShareDate() { return shareDate; }
    public void setShareDate(String shareDate) { this.shareDate = shareDate; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public boolean isExpired() { return isExpired; }
    public void setExpired(boolean expired) { isExpired = expired; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ShareRecord{id='" + id + "', certificateId='" + certificateId + "', recipient='" + recipientEmail + "', status='" + status + "'}";
    }
}

