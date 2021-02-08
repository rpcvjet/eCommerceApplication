package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    UserController userController;

    OrderController orderController;
    private UserRepository userRepository=mock(UserRepository.class);
    private CartRepository cartRepository=mock(CartRepository.class);
    private OrderRepository orderRepository= mock(OrderRepository.class);
    private BCryptPasswordEncoder encoder=mock(BCryptPasswordEncoder.class);

    @Before
    public void setup(){
        orderController = new OrderController();
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }
    private ResponseEntity<User> createUser(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Ken");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmpassword("testPassword");
        return userController.createUser(createUserRequest);
    }

    @Test
    public void submitTest(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        ResponseEntity<User> response = createUser();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = new User();
        user.setUsername("Ken");
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        Cart cart = new Cart();
        Item items = new Item();
        items.setDescription("laptop");
        items.setId(0L);
        items.setName("Macbook");
        items.setPrice(new BigDecimal(2000.00));
        List<Item> itemList = new ArrayList<>();
        itemList.add(items);
        cart.addItem(items);
        user.setCart(cart);

        ResponseEntity<UserOrder> orderResponse = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1,itemList.size());
        assertEquals("Macbook",items.getName());
    }
    @Test
    public void SubmitUnknowUserTest(){
        ResponseEntity<UserOrder> userOrderResponseEntity = orderController.submit("Biden");
        assertNotNull(userOrderResponseEntity);
        assertEquals(404, userOrderResponseEntity.getStatusCodeValue());
    }
    @Test
    public void findOrderHistoryByUserTest(){
        //create the hashed password
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        ResponseEntity<User> response = createUser();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        //create  user
        User user = new User();
        user.setUsername("Ken");
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        //create an order object
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);

        //create an item
        Item items = new Item();
        items.setDescription("food");
        items.setId(1L);
        items.setName("oatmeal");
        items.setPrice(new BigDecimal(5.0));

        //add item to Itemlist
        List<Item> itemList = new ArrayList<>();
        itemList.add(items);
        userOrder.setItems(itemList);
        userOrder.setTotal(new BigDecimal(5));
        userOrder.setId(1L);
        assertEquals(1, itemList.size());

        //add order to Orderlist
        List<UserOrder> userOrderList = new ArrayList<>();
        userOrderList.add(userOrder);
        when(orderRepository.findByUser(user)).thenReturn(userOrderList);
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

    }
}
