package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("Requesting findById with Id: {}", id);
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.info("Requesting findByUserName with Username: {}", username);
		User user = userRepository.findByUsername(username);
		if (user == null) {
			log.warn("User not found");
			ResponseEntity.notFound().build();
		}
		log.info("User found - username: '{}', id: {}", user.getUsername(), user.getId());
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		if (createUserRequest.getUsername() == null
			|| createUserRequest.getPassword() == null
			|| createUserRequest.getConfirmPassword() == null
		) {
			log.warn("Bad Request, due to wrong createUserRequest-variables.");
			log.debug("username == null: {}", createUserRequest.getUsername() == null);
			log.debug("password == null: {}", createUserRequest.getPassword() == null);
			log.debug("confirmPassword == null: {}", createUserRequest.getConfirmPassword() == null);
			return ResponseEntity.badRequest().build();
		}

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		log.info("User name set with '{}'", createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		log.info("Cart of user saved with id {}.", cart.getId());

		user.setCart(cart);
		log.info("Added cart {} to user {}.", cart.getId(), user.getId());

		if (createUserRequest.getPassword().length() < 7 ||
			!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) 
		{
			log.warn("Bad Request - Password strength not valid or missmatch between password and confirmPassword");
			log.debug("Password length: {}", createUserRequest.getPassword().length());
			log.debug("Password == ConfirmPassword?: {}", createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword()));
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		log.info("Encrypted password for user");
		userRepository.save(user);
		log.info("Saved user to userRepository");
		return ResponseEntity.ok(user);
	}
	
}
