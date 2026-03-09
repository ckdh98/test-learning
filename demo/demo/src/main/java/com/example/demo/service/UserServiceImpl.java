package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserSignUpRequest;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public UserResponse signUp(UserSignUpRequest request, String ipAddress) {
		String normalizedUserId = request.getUserId().trim();
		if (userRepository.existsByUserId(normalizedUserId)) {
			throw new IllegalStateException("이미 존재하는 아이디입니다.");
		}

		User user = new User();
		user.setUserId(normalizedUserId);
		user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
		user.setName(request.getName().trim());
		user.setIpAddress(ipAddress);

		userRepository.insertUser(user);
		return toResponse(userRepository.findByUserId(normalizedUserId));
	}

	@Override
	public UserResponse login(UserLoginRequest request) {
		User user = userRepository.findByUserId(request.getUserId().trim());
		if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
		}
		return toResponse(user);
	}

	@Override
	public List<UserResponse> findAllUsers() {
		return userRepository.findAllUsers().stream()
			.map(this::toResponse)
			.toList();
	}

	@Override
	public Optional<UserResponse> findUser(String userId) {
		return Optional.ofNullable(userRepository.findByUserId(userId))
			.map(this::toResponse);
	}

	@Override
	@Transactional
	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		if (!StringUtils.hasText(request.getName()) && !StringUtils.hasText(request.getPassword())) {
			throw new IllegalArgumentException("수정할 항목이 없습니다.");
		}

		User existingUser = userRepository.findByUserId(userId);
		if (existingUser == null) {
			throw new NoSuchElementException("회원 정보를 찾을 수 없습니다.");
		}

		User user = new User();
		user.setUserId(userId);
		if (StringUtils.hasText(request.getName())) {
			user.setName(request.getName().trim());
		}
		if (StringUtils.hasText(request.getPassword())) {
			user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
		}

		userRepository.updateUser(user);
		return toResponse(userRepository.findByUserId(userId));
	}

	@Override
	@Transactional
	public boolean deleteUser(String userId) {
		return userRepository.deleteUser(userId) > 0;
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(
			user.getUserId(),
			user.getName(),
			user.getCreatedAt(),
			user.getIpAddress()
		);
	}
}
