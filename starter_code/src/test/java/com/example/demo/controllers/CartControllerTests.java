package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {

    private CartController cartController;

    //Creates a mock object of the userRepo
    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        //injects the fields into our userController object
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        User testUser = new User();
        testUser.setUsername("test");
        testUser.setPassword("hashedPassword");
        testUser.setCart(new Cart());
        testUser.getCart().setUser(testUser);
        when(userRepo.findUserByUsername(anyString())).thenReturn(testUser);

        Item testItem = new Item();
        testItem.setName("testName");
        testItem.setDescription("test Description");
        testItem.setPrice(new BigDecimal("9.50"));
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(testItem));

    }

    @Test
    public void addToCartTest() {
        ModifyCartRequest mcc = new ModifyCartRequest();
        mcc.setItemId(1);
        mcc.setQuantity(2);
        mcc.setUsername("test");
        ResponseEntity<Cart> response = cartController.addTocart(mcc);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(new BigDecimal("19.00") ,cart.getTotal());
        assertEquals(2, cart.getItems().size());
        assertEquals("testName", cart.getItems().get(0).getName());

    }

    @Test
    public void removeFromCart() {
        ModifyCartRequest mcc = new ModifyCartRequest();
        mcc.setItemId(1);
        mcc.setQuantity(2);
        mcc.setUsername("test");
        cartController.addTocart(mcc);
        mcc.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromCart(mcc);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(new BigDecimal("9.50") ,cart.getTotal());
        assertEquals(1, cart.getItems().size());
        assertEquals("testName", cart.getItems().get(0).getName());
    }
}
