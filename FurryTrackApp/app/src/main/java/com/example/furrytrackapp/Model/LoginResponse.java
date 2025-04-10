package com.example.furrytrackapp.Model;

public class LoginResponse {
    private String token;
    private Users user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}