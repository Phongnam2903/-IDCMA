package com.example.idcma_project_prm392.model;

public class AuthResponse {
    private User user;
    private AuthToken authToken;

    public AuthResponse() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
