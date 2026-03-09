package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class UserSignUpRequest {

	@NotBlank(message = "userId is required")
	private String userId;

	@NotBlank(message = "password is required")
	private String password;

	@NotBlank(message = "name is required")
	private String name;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
