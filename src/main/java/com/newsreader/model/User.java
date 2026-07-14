package com.newsreader.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty username = new SimpleStringProperty(this, "username");
    private final StringProperty email = new SimpleStringProperty(this, "email");
    private final StringProperty password = new SimpleStringProperty(this, "password");
    private final StringProperty role = new SimpleStringProperty(this, "role");

    /**
     * Default constructor.
     */
    public User() {}

    /**
     * Parameterized constructor including user ID.
     */
    public User(int id, String username, String email, String password, String role) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setRole(role);
    }

    /**
     * Parameterized constructor excluding user ID (for new registrations).
     */
    public User(String username, String email, String password, String role) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setRole(role);
    }

    // ID
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // Username
    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }
    public StringProperty usernameProperty() { return username; }

    // Email
    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }

    // Password
    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value); }
    public StringProperty passwordProperty() { return password; }

    // Role
    public String getRole() { return role.get(); }
    public void setRole(String value) { role.set(value); }
    public StringProperty roleProperty() { return role; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }
}
