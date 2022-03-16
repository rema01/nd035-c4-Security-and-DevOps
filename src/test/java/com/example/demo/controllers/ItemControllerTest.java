package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);
    
    @Before
    public void setUp() {
        itemController = new ItemController();

        Item roundWidget = new Item();
        roundWidget.setId(1L);
        roundWidget.setName("Round Widget");
        roundWidget.setPrice(new BigDecimal(2.99));
        roundWidget.setDescription("The Round Widget");

        Item squareWidget = new Item();
        squareWidget.setId(2L);
        squareWidget.setName("Square Widget");
        squareWidget.setPrice(new BigDecimal(1.99));
        squareWidget.setDescription("The Square Widget");

        when(itemRepo.findAll()).thenReturn(Lists.newArrayList(roundWidget, squareWidget));
        when(itemRepo.findByName("Round Widget")).thenReturn(Lists.newArrayList(roundWidget));
        when(itemRepo.findById(2L)).thenReturn(Optional.of(squareWidget));

        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void item_found() {
        ResponseEntity<List<Item>> itemRes = itemController.getItemsByName("Round Widget");

        assertEquals(200, itemRes.getStatusCodeValue());
        assertNotNull(itemRes.getBody());
        
        List<Item> items = itemRes.getBody();
        assertEquals(1, items.size());

        Item item = items.get(0);
        assertEquals("Round Widget", item.getName());
        assertEquals(new BigDecimal(2.99), item.getPrice());
    }

    @Test
    public void item_not_found() {
        ResponseEntity<List<Item>> itemRes = itemController.getItemsByName("Lined Widget");

        assertEquals(404, itemRes.getStatusCodeValue());
        assertNull(itemRes.getBody());
    }

    @Test
    public void item_all() {
        ResponseEntity<List<Item>> itemRes = itemController.getItems();

        assertEquals(200, itemRes.getStatusCodeValue());
        assertNotNull(itemRes.getBody());

        List<Item> items = itemRes.getBody();
        assertEquals(2, items.size());
        assertEquals("Round Widget", items.get(0).getName());
        assertEquals(2L, items.get(1).getId().longValue());
        assertEquals("Square Widget", items.get(1).getName());
    }

    @Test
    public void item_by_id_found() {
        ResponseEntity<Item> itemRes = itemController.getItemById(2L);

        assertEquals(200, itemRes.getStatusCodeValue());
        assertNotNull(itemRes.getBody());
        
        Item item = itemRes.getBody();
        assertEquals("Square Widget", item.getName());
        assertEquals(new BigDecimal(1.99), item.getPrice());
    }

    @Test
    public void item_by_id_not_found() {
        ResponseEntity<Item> itemRes = itemController.getItemById(3L);

        assertEquals(404, itemRes.getStatusCodeValue());
        assertNull(itemRes.getBody());
    }
}
