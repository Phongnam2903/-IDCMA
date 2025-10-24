package com.example.idcma_project_prm392.model;

import java.util.List;

public class Profile {
    private String userId;
    private String slug;               // URL tuỳ chỉnh
    private List<String> certificateIds;
    private String publicUrl;

    public Profile() {}

    public Profile(String userId, String slug, List<String> certificateIds, String publicUrl) {
        this.userId = userId;
        this.slug = slug;
        this.certificateIds = certificateIds;
        this.publicUrl = publicUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<String> getCertificateIds() {
        return certificateIds;
    }

    public void setCertificateIds(List<String> certificateIds) {
        this.certificateIds = certificateIds;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }
}
