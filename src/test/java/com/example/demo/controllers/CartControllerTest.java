package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);
    
    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void add_to_cart() {
        User user = getUser();
        Item item1 = getItem_1();
        
        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setUsername(user.getUsername());
        cartReq.setItemId(item1.getId());
        cartReq.setQuantity(2);

        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepo.findById(item1.getId())).thenReturn(Optional.of(item1));

        ResponseEntity<Cart> cartRes = cartController.addTocart(cartReq);
        assertEquals(200, cartRes.getStatusCodeValue());
        assertNotNull(cartRes.getBody());
        Cart cart = cartRes.getBody();

        assertEquals(2, cart.getItems().size());
        assertEquals(new BigDecimal(7.98).setScale(2, RoundingMode.HALF_UP), cart.getTotal().setScale(2, RoundingMode.HALF_UP));
        assertEquals("test", cart.getUser().getUsername());
    }

    @Test
    public void add_to_cart_item_not_found() {
        User user = getUser();
        
        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setUsername(user.getUsername());
        cartReq.setItemId(4L);
        cartReq.setQuantity(2);

        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);

        ResponseEntity<Cart> cartRes = cartController.addTocart(cartReq);
        assertEquals(404, cartRes.getStatusCodeValue());
        assertNull(cartRes.getBody());
    }

    @Test
    public void add_to_cart_user_not_found() {
        Item item1 = getItem_1();
        
        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setUsername("Bob");
        cartReq.setItemId(item1.getId());
        cartReq.setQuantity(2);

        when(itemRepo.findById(item1.getId())).thenReturn(Optional.of(item1));

        ResponseEntity<Cart> cartRes = cartController.addTocart(cartReq);
        assertEquals(404, cartRes.getStatusCodeValue());
        assertNull(cartRes.getBody());
    }

    @Test
    public void remove_from_cart() {
        User user = getUser();
        Item item1 = getItem_1();
        
        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setUsername(user.getUsername());
        cartReq.setItemId(item1.getId());
        cartReq.setQuantity(2);

        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepo.findById(item1.getId())).thenReturn(Optional.of(item1));

        ResponseEntity<Cart> cartRes = cartController.addTocart(cartReq);
        assertEquals(200, cartRes.getStatusCodeValue());
        assertNotNull(cartRes.getBody());
        Cart cart = cartRes.getBody();

        assertEquals(2, cart.getItems().size());
        assertEquals(new BigDecimal(7.98).setScale(2, RoundingMode.HALF_UP), cart.getTotal().setScale(2, RoundingMode.HALF_UP));
        assertEquals("test", cart.getUser().getUsername());

        ModifyCartRequest cartReqToRemove = new ModifyCartRequest();
        cartReqToRemove.setUsername(user.getUsername());
        cartReqToRemove.setItemId(item1.getId());
        cartReqToRemove.setQuantity(0);

        ResponseEntity<Cart> cartResAfterRemoving = cartController.removeFromcart(cartReq);
        assertEquals(200, cartResAfterRemoving.getStatusCodeValue());
        assertNotNull(cartResAfterRemoving.getBody());
        Cart cartAfterRemoving = cartResAfterRemoving.getBody();

        assertEquals(0, cartAfterRemoving.getItems().size());
        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.HALF_UP), cartAfterRemoving.getTotal().setScale(2, RoundingMode.HALF_UP));
        assertEquals("test", cartAfterRemoving.getUser().getUsername());
    }

    @Test
    public void remove_from_cart_item_not_found() {
        User user = getUser();
        Item item1 = getItem_1();
        
        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setUsername(user.getUsername());
        cartReq.setItemId(item1.getId());
        cartReq.setQuantity(2);

        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepo.findById(item1.getId())).thenReturn(Optional.of(item1));

        ResponseEntity<Cart> cartRes = cartController.addTocart(cartReq);
        assertEquals(200, cartRes.getStatusCodeValue());
        assertNotNull(cartRes.getBody());
        Cart cart = cartRes.getBody();

        assertEquals(2, cart.getItems().size());
        assertEquals(new BigDecimal(7.98).setScale(2, RoundingMode.HALF_UP), cart.getTotal().setScale(2, RoundingMode.HALF_UP));
        assertEquals("test", cart.getUser().getUsername());

        ModifyCartRequest cartReqToRemove = new ModifyCartRequest();
        cartReqToRemove.setUsername(user.getUsername());
        cartReqToRemove.setItemId(5L);

        ResponseEntity<Cart> cartResAfterRemoving = cartController.removeFromcart(cartReq);
        assertEquals(200, cartResAfterRemoving.getStatusCodeValue());
        assertNotNull(cartResAfterRemoving.getBody());

        Cart cartAfterRemoving = cartResAfterRemoving.getBody();
        assertEquals(0, cartAfterRemoving.getItems().size());
    }

    @Test
    public void remove_from_cart_user_not_found() {
        User user = getUser();
        Item item1 = getItem_1();
        
        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setUsername(user.getUsername());
        cartReq.setItemId(item1.getId());
        cartReq.setQuantity(2);

        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepo.findById(item1.getId())).thenReturn(Optional.of(item1));

        ResponseEntity<Cart> cartRes = cartController.addTocart(cartReq);
        assertEquals(200, cartRes.getStatusCodeValue());
        assertNotNull(cartRes.getBody());
        Cart cart = cartRes.getBody();

        assertEquals(2, cart.getItems().size());
        assertEquals(new BigDecimal(7.98).setScale(2, RoundingMode.HALF_UP), cart.getTotal().setScale(2, RoundingMode.HALF_UP));
        assertEquals("test", cart.getUser().getUsername());

        ModifyCartRequest cartReqToRemove = new ModifyCartRequest();
        cartReqToRemove.setUsername(user.getUsername());
        cartReqToRemove.setItemId(item1.getId());
        cartReqToRemove.setQuantity(0);

        ResponseEntity<Cart> cartResAfterRemoving = cartController.removeFromcart(cartReq);
        assertEquals(200, cartResAfterRemoving.getStatusCodeValue());
        assertNotNull(cartResAfterRemoving.getBody());
        
        Cart cartAfterRemoving = cartResAfterRemoving.getBody();
        assertEquals(0, cartAfterRemoving.getItems().size());
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("hashedTestPassword");
        return user;
    }

    private Item getItem_1() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Product 1");
        item.setPrice(new BigDecimal(3.99));
        return item;
    }

    private Item getItem_2() {
        Item item = new Item();
        item.setId(2L);
        item.setName("Product 2");
        item.setPrice(new BigDecimal(1.49));
        return item;
    }
}
