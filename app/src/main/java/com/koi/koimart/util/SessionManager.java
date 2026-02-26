package com.koi.koimart.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF = "koi_mart_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";

    private final SharedPreferences sp;

    public SessionManager(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    // UPDATED: store name as well
    public void login(int userId, String name, String email) {
        sp.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public void logout() {
        sp.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return sp.getInt(KEY_USER_ID, -1) != -1;
    }

    public int getUserId() {
        return sp.getInt(KEY_USER_ID, -1);
    }

    // NEW getters (this fixes ProfileActivity error)
    public String getUserName() {
        return sp.getString(KEY_NAME, "");
    }

    public String getUserEmail() {
        return sp.getString(KEY_EMAIL, "");
    }
}
