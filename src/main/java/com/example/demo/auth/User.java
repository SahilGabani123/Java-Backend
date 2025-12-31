package com.example.demo.auth;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

	private @Id @GeneratedValue Long id;

	@Column(name = "name", nullable = false, unique = true)
	@Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
	private String name;

	private String position;

	@Column(name = "joining_date", nullable = false)
	@JsonFormat(pattern = "dd/MM/yyyy", timezone = "UTC")
	@PastOrPresent(message = "Joining date cannot be in the future")
	private Instant joiningDate;

	@NotNull(message = "Employee field must not be null")
	@NotEmpty(message = "Employee field must not be empty")
	private String field;

	@NotNull(message = "email must not be null")
	@NotEmpty(message = "email must not be empty")
	@Email
	@JsonProperty("email")
	private String email;

	@NotNull(message = "Phone number must not be null")
	@NotEmpty(message = "Phone number must not be empty")
	@JsonProperty("phone_number")
	@Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
	private String phoneNumber;

	@JsonIgnore
	@Column(nullable = false)
	private String password;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
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
