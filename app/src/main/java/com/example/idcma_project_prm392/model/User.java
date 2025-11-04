package com.example.idcma_project_prm392.model;

public class User {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String profileImage;

    public User() {}

    // Constructor 3 tham số (để dùng trong RegisterActivity)
    public User(String id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }
    public User(String fullName, String email, String password, String role, String profileImage) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.profileImage = profileImage;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
