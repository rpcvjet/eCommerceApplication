package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    UserController userController;
    ItemController itemController;
    OrderController orderController;
    private UserRepository userRepository=mock(UserRepository.class);
    private CartRepository cartRepository=mock(CartRepository.class);
    private OrderRepository orderRepository= mock(OrderRepository.class);
    private ItemRepository itemRepository= mock(ItemRepository.class);
    private BCryptPasswordEncoder encoder=mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp()
    {
        orderController = new OrderController();
        userController = new UserController();
        itemController = new ItemController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    public List<Item> createItem() {
        //set up for this test
        Item items = new Item();
        items.setDescription("Stuffed Toy");
        items.setId(0L);
        items.setName("Elmo");
        items.setPrice(new BigDecimal(25.00));
        List<Item> itemList = new ArrayList<>();
        itemList.add(items);
        return itemList;
    }

    @Test
    public void getAllItemsTest()
    {
        List<Item> list = this.createItem();
        when(itemRepository.findAll()).thenReturn(list);

        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void ItemDoesNotExistTest()
    {
        ResponseEntity<Item> response = itemController.getItemById(77L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemByIdTest()
    {
        Item item = new Item();
        item.setId(5L);
        item.setName("PS4");
        item.setDescription("PS4 Game Console");
        item.setPrice(BigDecimal.valueOf(5000.0));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ResponseEntity<Item> responseEntity = itemController.getItemById(5L);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void findItemByNameTest(){
        List<Item> list = this.createItem();
        when(itemRepository.findByName(anyString())).thenReturn(list);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("Elmo");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }


}
