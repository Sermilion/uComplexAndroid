package org.ucomplex.ucomplex.CommonDependencies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.javatuples.Pair;
import org.ucomplex.ucomplex.Domain.Users.User;
import org.ucomplex.ucomplex.Domain.Users.UserInterface;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 01/11/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public class FacadePreferences {

    private static final String KEY_PREF_USER_ROLE = "userRole";
    private static final String KEY_PREF_LOGIN_DATA = "userRole";
    private static final String KEY_PREF_LOGGED_USER = "loggedUser";
    public static final String KEY_PREF_PROFILE_PHOTO = "profilePhoto";
    private static final String KEY_PREF_LOGIN = "login";
    private static final String KEY_PREF_PASSWORD = "password";

    public static void setRoleToPref(Context mContext, int role) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putInt(KEY_PREF_USER_ROLE, role);
        editor.apply();
    }

    public static int getRoleFromPref(Context mContext) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return pref.getInt(KEY_PREF_USER_ROLE, -2);
    }

    public static String getLoginDataFromPref(Context mContext) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return pref.getString(KEY_PREF_LOGIN_DATA, "");
    }

    public static void setLoginDataToPref(Context mContext, String loginData) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(KEY_PREF_LOGIN_DATA, loginData);
        editor.apply();
    }

    public static User getUserDataFromPref(Context mContext) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = pref.getString(KEY_PREF_LOGGED_USER, "");
        return gson.fromJson(json, User.class);
    }

    public static void setUserDataToPref(Context mContext, UserInterface user) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(KEY_PREF_LOGGED_USER, json);
        editor.apply();
    }

    public static void deleteFromPref(Context context, String typeStr) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(typeStr);
        editor.apply();
    }

    public static void clearPref(Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    public static void savePrevLoginInfo(Context context, String login, String password){
        SharedPreferences pref = context.getSharedPreferences("prevLogin",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_PREF_LOGIN, login);
        editor.putString(KEY_PREF_PASSWORD, password);
        editor.apply();
    }

    public static Pair<String, String> getPrevLoginInfo(Context context){
        SharedPreferences pref = context.getSharedPreferences("prevLogin",Context.MODE_PRIVATE);
        return new Pair<>(pref.getString(KEY_PREF_LOGIN,""),pref.getString(KEY_PREF_PASSWORD,""));
    }

}
