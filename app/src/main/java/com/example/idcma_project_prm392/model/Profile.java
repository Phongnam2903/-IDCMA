package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Profile {
    @DocumentId
    private String id;
    private String userId;
    private String bio;
    private String headline;
    private String publicSlug; // for public showcase
    private boolean isPublic;

    public Profile() {}

    public Profile(String userId, String bio, String headline, String publicSlug, boolean isPublic) {
        this.userId = userId;
        this.bio = bio;
        this.headline = headline;
        this.publicSlug = publicSlug;
        this.isPublic = isPublic;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public String getPublicSlug() { return publicSlug; }
    public void setPublicSlug(String publicSlug) { this.publicSlug = publicSlug; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
}
