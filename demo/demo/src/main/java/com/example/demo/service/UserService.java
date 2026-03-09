package com.example.demo.service;

import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserSignUpRequest;
import com.example.demo.dto.UserUpdateRequest;
import java.util.List;
import java.util.Optional;

public interface UserService {

	UserResponse signUp(UserSignUpRequest request, String ipAddress);

	UserResponse login(UserLoginRequest request);

	List<UserResponse> findAllUsers();

	Optional<UserResponse> findUser(String userId);

	UserResponse updateUser(String userId, UserUpdateRequest request);

	boolean deleteUser(String userId);
}
