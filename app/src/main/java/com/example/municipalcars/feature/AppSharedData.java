package com.example.municipalcars.feature;

import static android.content.Context.MODE_PRIVATE;

import com.example.municipalcars.APP;
import com.example.municipalcars.R;
import com.example.municipalcars.model.UserData;
import com.google.gson.Gson;

public class AppSharedData {

    private static Gson gson = new Gson();
    public static final String SHARED_APP_DATA = APP.getInstance().getString(R.string.app_name);
    public static final String SHARED_USER_DATA = "user";
    private static final String SHARED_IS_USER_LOGIN = "is_user_login";
    private static final String SHARED_IS_First = "First";
    private static final String SHARED_IS_USER_LOGIN_BY_SOCIAL = "login_social";
    private static final String SHARED_PASSWORD = "password";
    private static final String SHARED_LOCAL_Folder = "LOCAL_Folder";
    private static final String SHARED_IS_APP_OPENED_BEFORE = "is_opened";
    public static final String CODES = "CodeStrings";

    public static final String CODES1 = "CodeStrings1";
    public static final String CODES2 = "CodeStrings2";
    public static final String CODES3 = "CodeStrings3";

    public AppSharedData() {
    }


    public static void setUserData(UserData userData) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit().putString(SHARED_USER_DATA, gson.toJson(userData)).apply();
    }

    public static UserData getUserData() {
        UserData mUser = gson.fromJson(APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).getString(SHARED_USER_DATA, null), UserData.class);
        return mUser;
    }

    public static boolean isUserLogin() {
        return APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).getBoolean(SHARED_IS_USER_LOGIN, false);
    }

    public static void setUserLogin(boolean login) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit().putBoolean(SHARED_IS_USER_LOGIN, login).apply();
    }

    public static boolean isFirst() {
        return APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).getBoolean(SHARED_IS_First, true);
    }

    public static void setFirst(boolean login) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit().putBoolean(SHARED_IS_First, login).apply();
    }

    public static boolean isLoginBySocial() {
        return APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).getBoolean(SHARED_IS_USER_LOGIN_BY_SOCIAL, false);
    }

    public static void setLoginBySocial(boolean login) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit().putBoolean(SHARED_IS_USER_LOGIN_BY_SOCIAL, login).apply();
    }

    public static void setPassword(String password) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit()
                .putString(SHARED_PASSWORD, password).apply();
    }

    public static String getPassword() {
        return APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getString(SHARED_PASSWORD, "");
    }

    public static void setCodes(String code) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit().putString(CODES, code).apply();
    }

    public static String getCodes() {
        return APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).getString(CODES, null);
    }


}
