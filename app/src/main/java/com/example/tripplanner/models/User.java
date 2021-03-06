package com.example.tripplanner.models;

public class User {
    public String email, fullName, username, password, picUrl;

    public User() {}

    public User(String email, String password, String fullName, String username, String picUrl) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.picUrl = picUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPicUrl() {
        return picUrl;
    }
}
