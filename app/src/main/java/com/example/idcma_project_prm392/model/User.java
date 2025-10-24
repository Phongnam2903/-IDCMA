package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    @DocumentId
    private String id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private boolean twoFactorEnabled;

    public User() {}

    public User(String fullName, String email, String avatarUrl, boolean twoFactorEnabled) {
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
}
