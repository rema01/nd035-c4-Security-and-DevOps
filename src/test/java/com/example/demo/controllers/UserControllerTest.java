package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before 
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testpassword")).thenReturn("thisIsHashed");
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("testpassword");
        req.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(req);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void create_user_password_unmatched() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("testpassword");
        req.setConfirmPassword("testpasswordwrong");

        final ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void find_by_id() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("Testi");
        expectedUser.setPassword("TestiPassword");
        
        when(encoder.encode("TestiPassword")).thenReturn("hashedTestiPassword");
        when(userRepo.findById(1L)).thenReturn(Optional.of(expectedUser));

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Testi");
        userRequest.setPassword("TestiPassword");
        userRequest.setConfirmPassword("TestiPassword");
        
        final ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final ResponseEntity<User> result = userController.findById(1L);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());

        User actualUser = result.getBody();
        assertNotNull(actualUser);
        assertEquals(1, actualUser.getId());
        assertEquals("Testi", actualUser.getUsername());
        assertEquals("TestiPassword", actualUser.getPassword());
    }

    @Test
    public void find_by_username() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("Testi");
        expectedUser.setPassword("TestiPassword");
        
        when(encoder.encode("TestiPassword")).thenReturn("hashedTestiPassword");
        when(userRepo.findByUsername("Testi")).thenReturn(expectedUser);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Testi");
        userRequest.setPassword("TestiPassword");
        userRequest.setConfirmPassword("TestiPassword");
        
        final ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final ResponseEntity<User> result = userController.findByUserName("Testi");

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());

        User actualUser = result.getBody();
        assertNotNull(actualUser);
        assertEquals(1, actualUser.getId());
        assertEquals("Testi", actualUser.getUsername());
        assertEquals("TestiPassword", actualUser.getPassword());
    }
}
