package com.example.demo.auth;

import com.example.demo.auth.model.LoginRequest;
import com.example.demo.auth.model.SignupRequest;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.PhoneAlreadyExistsException;
import com.example.demo.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
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

		return jwtUtil.generateToken(user);
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}
}
