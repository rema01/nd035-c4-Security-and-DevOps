package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class OrderControllerTest {
    private OrderController orderController;

    private OrderRepository orderRepo = mock(OrderRepository.class);

    private UserController userController = mock(UserController.class);

    private UserRepository userRepo = mock(UserRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submit_an_order() throws Exception {
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setUsername("test");
        userReq.setPassword("testPassword");
        userReq.setConfirmPassword("testPassword");

        User actualUser = new User();
        actualUser.setId(1L);
        actualUser.setUsername("test");
        actualUser.setPassword("hashedTestPassword");
        
        when(userController.createUser(userReq)).thenReturn(ResponseEntity.ok(actualUser));
        ResponseEntity<User> userRes = userController.createUser(userReq);
        if (userRes.getBody() == null) {
            throw new Exception("User couldn't be created.");
        }
        User user = userRes.getBody();
        
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("This is Test Item No. 1");
        item1.setPrice(new BigDecimal(2.5));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("This is Test Item No. 2");
        item2.setPrice(new BigDecimal(3.99));

        Cart cart = new Cart();
        cart.addItem(item1);
        cart.addItem(item2);

        actualUser.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(actualUser);
        ResponseEntity<UserOrder> orderRes = orderController.submit("test");
        assertEquals(200, orderRes.getStatusCodeValue());
        UserOrder actualUserOrder = orderRes.getBody();
        assertNotNull(actualUserOrder);
        assertEquals(new BigDecimal(6.49), actualUserOrder.getTotal());
        assertEquals(2, actualUserOrder.getItems().size());
    }
}
