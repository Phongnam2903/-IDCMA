package com.example.idcma_project_prm392.model;

import java.util.List;

public class Profile {
    private String id;
    private String userId;
    private String slug;               // custom URL slug
    private List<String> certificateIds;
    private String publicUrl;
    private boolean isPublic;

    public Profile() {}

    public Profile(String userId, String slug, List<String> certificateIds, String publicUrl, boolean isPublic) {
        this.userId = userId;
        this.slug = slug;
        this.certificateIds = certificateIds;
        this.publicUrl = publicUrl;
        this.isPublic = isPublic;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    @Override
    public String toString() {
        return "Profile{id='" + id + "', userId='" + userId + "', slug='" + slug + "', publicUrl='" + publicUrl + "'}";
    }
}
