package com.example.idcma_project_prm392.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager để quản lý user session (thay thế FirebaseAuth.getCurrentUser())
 */
public class SessionManager {
    private static final String PREF_NAME = "IDCMA_SESSION";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    public void createSession(String userId, String email, String fullName) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, fullName);
        editor.commit();
    }
    
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }
    
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }
    
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }
    
    public void logout() {
        editor.clear();
        editor.commit();
    }
}

