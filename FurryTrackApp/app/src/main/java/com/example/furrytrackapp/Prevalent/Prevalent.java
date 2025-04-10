package com.example.furrytrackapp.Prevalent;

import com.example.furrytrackapp.Model.Users;
import io.paperdb.Paper;

public class Prevalent {
    public static Users currentOnlineUser;
    public static String currentToken;
    public static final String USER_EMAIL_KEY = "UserEmail";
    public static final String USER_PASSWORD_KEY = "UserPassword";

    public static void saveUserData(String email, String password) {
        Paper.book().write(USER_EMAIL_KEY, email);
        Paper.book().write(USER_PASSWORD_KEY, password);
    }

    public static void clearUserData() {
        Paper.book().delete(USER_EMAIL_KEY);
        Paper.book().delete(USER_PASSWORD_KEY);
        currentOnlineUser = null;
    }

    public static boolean isUserLoggedIn() {
        return Paper.book().contains(USER_EMAIL_KEY) || currentOnlineUser != null;
    }
}