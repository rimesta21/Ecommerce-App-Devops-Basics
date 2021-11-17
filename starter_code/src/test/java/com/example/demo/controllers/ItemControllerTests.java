package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTests {

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void getItemsByName() {
        Item testItem = new Item();
        testItem.setName("testName");
        testItem.setDescription("test Description");
        testItem.setPrice(new BigDecimal("9.50"));
        when(itemRepo.findByName(anyString())).thenReturn(List.of(testItem));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("testName");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals("testName", items.get(0).getName());



    }

    @Test
    public void getItemById() {
        Item testItem = new Item();
        testItem.setId(0L);
        testItem.setName("testName");
        testItem.setDescription("test Description");
        testItem.setPrice(new BigDecimal("9.50"));
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(testItem));

        ResponseEntity<Item> response = itemController.getItemById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(0L, item.getId());
        assertEquals("testName", item.getName());
    }
}
