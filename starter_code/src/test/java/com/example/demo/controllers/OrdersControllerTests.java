package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrdersControllerTests {

    private OrderController orderController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submitOrder() {
        User testUser = new User();
        testUser.setUsername("test");
        testUser.setPassword("hashedPassword");
        testUser.setCart(new Cart());
        Cart cart = testUser.getCart();
        cart.setUser(testUser);
        Item testItem = new Item();
        testItem.setName("testName");
        testItem.setDescription("test Description");
        testItem.setPrice(new BigDecimal("9.50"));
        cart.addItem(testItem);
        when(userRepo.findUserByUsername(anyString())).thenReturn(testUser);

        ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertEquals("testName", order.getItems().get(0).getName());
        assertEquals("test", order.getUser().getUsername());
    }

    @Test
    public void viewOrderHistory() {
        User testUser = new User();
        testUser.setUsername("test");
        testUser.setPassword("hashedPassword");
        testUser.setCart(new Cart());
        when(userRepo.findUserByUsername(anyString())).thenReturn(testUser);
        Item testItem = new Item();
        testItem.setName("testName");
        testItem.setDescription("test Description");
        testItem.setPrice(new BigDecimal("9.50"));
        Item testItem2 = new Item();
        testItem2.setName("testName2");
        testItem2.setDescription("test Description2");
        testItem2.setPrice(new BigDecimal("10.50"));

        UserOrder order1 = new UserOrder();
        order1.setItems(List.of(testItem));

        UserOrder order2 = new UserOrder();
        order2.setItems(Arrays.asList(testItem, testItem2));

        when(orderRepo.findByUser(testUser)).thenReturn(Arrays.asList(order1, order2));
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> history = response.getBody();
        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getItems().size());
        assertEquals(2, history.get(1).getItems().size());
        assertEquals("testName", history.get(0).getItems().get(0).getName());
        assertEquals("testName", history.get(1).getItems().get(0).getName());
        assertEquals("testName2", history.get(1).getItems().get(1).getName());
    }
}
