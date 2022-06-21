package com.example.tripplanner;

public class User {
    public String email, fullName, username, password;

    public User() {}

    public User(String email, String password, String fullName, String username) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
