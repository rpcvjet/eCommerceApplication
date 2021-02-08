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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private UserController userController;
    private ItemController itemController;
    private OrderController orderController;
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);


    @Before
    public void setUp()
    {
        userController = new UserController();
        itemController = new ItemController();
        cartController = new CartController();

        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void addToCartTest() {
        //initialize all the things
        User user = new User();
        Cart cart = new Cart();
        Item items = new Item();

        //set the user first
        user.setUsername("Ken");
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        //create item in inventory
        items.setDescription("PS4");
        items.setId(0L);
        items.setName("PlayStation 4");
        items.setPrice(new BigDecimal(5000.00));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(items));

        //add the item to the cart
        cart.addItem(items);
        //set cart to the user
        user.setCart(cart);

        //initialize and put set items in the request object
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        // this must match the ID created above
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setUsername("Ken");
        // how many?
        modifyCartRequest.setQuantity(1);

        //make the official request
        ResponseEntity<Cart> orderResponse = cartController.addTocart(modifyCartRequest);

        assertNotNull(orderResponse);
        assertEquals(200, orderResponse.getStatusCodeValue());

    }

    @Test
    public void removeFromCartTest(){
        User user = new User();
        Cart cart = new Cart();
        Item items = new Item();

        user.setUsername("Ken");

        items.setDescription("Stuffed Toy");
        items.setId(8L);
        items.setName("Elmo");
        items.setPrice(new BigDecimal(25.00));

        cart.addItem(items);
        user.setCart(cart);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(8L);
        modifyCartRequest.setUsername("Ken");
        modifyCartRequest.setQuantity(1);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(items));

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }
}
