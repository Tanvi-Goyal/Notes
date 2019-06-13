package com.example.tanvi.notes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    // Sharedpref file name
    private static final String PREF_NAME = "login";

    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String IS_LOGGEDIN = "user";

    // Shared Preferences
    private final SharedPreferences pref;
    // Editor for Shared preferences
    private final SharedPreferences.Editor editor;

    // Constructor
    public SharedPrefs(Context context) {
        // Context
        // Shared pref mode
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String email, String name) {
        // Storing login value as TRUE
        editor.putString(EMAIL, email);
        editor.putString(NAME, name);
        editor.putBoolean(IS_LOGGEDIN, true);

        // commit changes
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(EMAIL, null);
    }

    public String getName() {
        return pref.getString(NAME, null);
    }

    public Boolean isLoggedIn() { return  pref.getBoolean(IS_LOGGEDIN, false); }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }
}
