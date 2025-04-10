package com.example.furrytrackapp.Model;

import java.util.UUID;

public class Users {
    private String id;
    private String username;
    private String email;
    private String password;
    private String image;
    private String role;

    public Users() {
    }

    public Users(String id, String username, String email, String password, String image, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
        this.role = role;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getName() {
        return 0;
    }

    public Object getToken() {
        return null;
    }
}