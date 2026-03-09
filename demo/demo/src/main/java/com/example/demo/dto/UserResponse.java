package com.example.demo.dto;

import java.time.LocalDateTime;

public class UserResponse {

	private final String userId;
	private final String name;
	private final LocalDateTime createdAt;
	private final String ipAddress;

	public UserResponse(String userId, String name, LocalDateTime createdAt, String ipAddress) {
		this.userId = userId;
		this.name = name;
		this.createdAt = createdAt;
		this.ipAddress = ipAddress;
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getIpAddress() {
		return ipAddress;
	}
}
