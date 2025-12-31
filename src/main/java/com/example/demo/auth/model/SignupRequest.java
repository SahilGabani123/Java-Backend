package com.example.demo.auth.model;

import java.time.Instant;

import jakarta.validation.constraints.*;

public class SignupRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String field;

    private String position;

    @NotNull
    @PastOrPresent
    private Instant joiningDate;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String phoneNumber;

    @Size(min = 6)
    private String password;

    // Getters & Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public Instant getJoiningDate() {
        return joiningDate;
    }
    public void setJoiningDate(Instant joiningDate) {
        this.joiningDate = joiningDate;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
