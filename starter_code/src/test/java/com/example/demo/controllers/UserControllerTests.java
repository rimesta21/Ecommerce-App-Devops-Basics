package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTests {
    
    private UserController userController;
    
    //Creates a mock object of the userRepo
    private UserRepository userRepo = mock(UserRepository.class);
    
    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    
    @Before
    public void setUp() {
        userController = new UserController();
        //injects the fields into our userController object
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }
    

    @Test
    @Order(1)
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest ur = new CreateUserRequest();
        ur.setUsername("test");
        ur.setPassword("testPassword");
        ur.setConfirmPassword("testPassword");
        
        ResponseEntity<User> response = userController.createUser(ur);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
                
    }

    /*
    Not going to test findById and findByUserName because you really can't with mockito without doing when() and making
    return what I want by default.
     */

}
