package com.vakoze.lib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.vakoze.authentication.LoginActivity;
import com.vakoze.models.User;

/**
 * Created by capp on 23/01/2018.
 */

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "vakozesharedpref";
    private static final String KEY_NOM = "keynom";
    private static final String KEY_PRENOM = "keyprenom";
    private static final String KEY_PROFILE_PIC = "keyprofilepic";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_PHONE = "keyphone";
    private static final String KEY_UID = "keyu_id";
    private static final String KEY_ID = "keyid";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_ID, user.getId());
        editor.putString(KEY_UID, user.getU_id());
        editor.putString(KEY_NOM, user.getNom());
        editor.putString(KEY_PRENOM, user.getPrenom());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());

        editor.putString(KEY_PROFILE_PIC, user.getProfile_pic());
        editor.apply();
    }

    public void userProfileLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_ID, user.getId());
        editor.putString(KEY_UID, user.getU_id());
        editor.putString(KEY_NOM, user.getNom());
        editor.putString(KEY_PRENOM, user.getPrenom());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());

        editor.putString(KEY_PROFILE_PIC, user.getProfile_pic());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NOM, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Long def = (long) -1;
        return new User(

                sharedPreferences.getLong(KEY_ID, 0L),
                sharedPreferences.getString(KEY_UID, null),
                sharedPreferences.getString(KEY_NOM, null),
                sharedPreferences.getString(KEY_PRENOM, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PHONE, null),

                sharedPreferences.getString(KEY_PROFILE_PIC,null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(mCtx, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(i);
    }
    public void clear() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
