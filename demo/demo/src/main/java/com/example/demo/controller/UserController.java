package com.example.demo.controller;

import com.example.demo.config.ClientIpResolver;
import com.example.demo.dto.MessageResponse;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserSignUpRequest;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signUp(
		@Valid @RequestBody UserSignUpRequest request,
		HttpServletRequest httpServletRequest
	) {
		try {
			UserResponse response = userService.signUp(
				request,
				ClientIpResolver.resolveIpv4(httpServletRequest)
			);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalStateException exception) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
		}
	}

	@PostMapping("/login")
	public UserResponse login(@Valid @RequestBody UserLoginRequest request) {
		try {
			return userService.login(request);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
		}
	}

	@GetMapping
	public List<UserResponse> findAllUsers() {
		return userService.findAllUsers();
	}

	@GetMapping("/{userId}")
	public UserResponse findUser(@PathVariable String userId) {
		return userService.findUser(userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
	}

	@PutMapping("/{userId}")
	public UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
		try {
			return userService.updateUser(userId, request);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
		} catch (NoSuchElementException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
		}
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<MessageResponse> deleteUser(@PathVariable String userId) {
		if (!userService.deleteUser(userId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.");
		}
		return ResponseEntity.ok(new MessageResponse("회원이 삭제되었습니다."));
	}
}
