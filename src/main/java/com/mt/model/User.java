package com.mt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class User {

    private long userId;

    @JsonProperty(required = true)
    private String userName;

    @JsonProperty(required = true)
    private String emailAddress;

    public User() {
    }

    public User(String userName, String emailAddress) {
        this.userName = userName;
        this.emailAddress = emailAddress;
    }

    public User(long userId, String userName, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.emailAddress = emailAddress;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                Objects.equals(userName, user.userName) &&
                Objects.equals(emailAddress, user.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, emailAddress);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }

}
