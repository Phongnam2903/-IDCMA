package com.example.idcma_project_prm392.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.idcma_project_prm392.database.Converters;
import java.util.List;

/**
 * Entity class cho Profile trong Room Database
 */
@Entity(tableName = "profiles")
@TypeConverters(Converters.class)
public class ProfileEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "slug")
    private String slug; // custom URL slug

    @ColumnInfo(name = "certificate_ids")
    private List<String> certificateIds;

    @ColumnInfo(name = "public_url")
    private String publicUrl;

    @ColumnInfo(name = "is_public")
    private boolean isPublic;

    public ProfileEntity() {}

    @Ignore
    public ProfileEntity(String userId, String slug, List<String> certificateIds, 
                        String publicUrl, boolean isPublic) {
        this.userId = userId;
        this.slug = slug;
        this.certificateIds = certificateIds;
        this.publicUrl = publicUrl;
        this.isPublic = isPublic;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public List<String> getCertificateIds() { return certificateIds; }
    public void setCertificateIds(List<String> certificateIds) { this.certificateIds = certificateIds; }

    public String getPublicUrl() { return publicUrl; }
    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
}

