package com.example.demo.auth;

import com.example.demo.auth.model.LoginRequest;
import com.example.demo.auth.model.SignupRequest;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.PhoneAlreadyExistsException;
import com.example.demo.security.JwtService;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.TokenBlacklistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final JwtService jwtService;
	private final TokenBlacklistService tokenBlacklistService;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, JwtService jwtService, TokenBlacklistService tokenBlacklistService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.jwtService = jwtService;
		this.tokenBlacklistService = tokenBlacklistService;
	}


	public User signup(SignupRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already registered");
		}

		if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
			throw new PhoneAlreadyExistsException("Phone number already registered");
		}

		User user = new User();
		user.setName(request.getName());
		user.setField(request.getField());
		user.setPosition(request.getPosition());
		user.setJoiningDate(request.getJoiningDate());
		user.setEmail(request.getEmail());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		return userRepository.save(user);
	}

	public String login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid email or password");
		}

		// Blacklist old token if user has an active token (single login functionality)
		if (user.getActiveToken() != null && !user.getActiveToken().isEmpty()) {
			try {
				tokenBlacklistService.blacklist(user.getActiveToken(), jwtService.getExpiration(user.getActiveToken()));
			} catch (Exception e) {
				// Ignore errors when blacklisting old token
			}
		}

		// Generate new token and store it as active token
		String newToken = jwtUtil.generateToken(user);
		user.setActiveToken(newToken);
		userRepository.save(user);

		return newToken;
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	public User save(User user) {
		return userRepository.save(user);
	}
}
