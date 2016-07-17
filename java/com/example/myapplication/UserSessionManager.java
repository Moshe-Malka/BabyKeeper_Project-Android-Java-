package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Moshe Malka on 22/05/2016.
 */
/*
    this class handel's user sessions in the Baby-keeper App
 */
public class UserSessionManager {
    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = Context.MODE_PRIVATE;

    // Shared pref file name
    private static final String PREFER_NAME = "babykeeper_user_login_Pref";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // email (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // password (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    // raspberry ID (make variable public to access from outside)
    public static final String KEY_RASPBERRY_PRODUCT_KEY = "raspberry_product_key";

    // Constructor
    public UserSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    //Create login session
    public void createUserLoginSession(String email, String password, String raspberry_product_key){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);

        // Storing raspberry product key in pref
        editor.putString(KEY_RASPBERRY_PRODUCT_KEY, raspberry_product_key);

        // commit changes
        editor.apply();
    }

    /*
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginAcvtivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }

    /*
     /*
     *Get stored session data
     */
    public HashMap<String, String> getUserDetails(){

        //Use hash map to store user credentials
        HashMap<String, String> user = new HashMap<>();

        // user email
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user password
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // user raspberry pi id
        user.put(KEY_RASPBERRY_PRODUCT_KEY, pref.getString(KEY_RASPBERRY_PRODUCT_KEY, null));
        // return user
        return user;

    }

    /*
     * Clear session details
     */
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.apply();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginAcvtivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}

