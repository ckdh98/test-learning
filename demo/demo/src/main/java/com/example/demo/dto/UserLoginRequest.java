package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequest {

	@NotBlank(message = "userId is required")
	private String userId;

	@NotBlank(message = "password is required")
	private String password;

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
}
