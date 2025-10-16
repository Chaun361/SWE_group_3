package com.example.demo.Cart.service;

import com.example.demo.Cart.dto.CartDTO;
import com.example.demo.Cart.exception.ResourceNotFoundException;
import com.example.demo.Cart.exception.StockException;
import com.example.demo.Cart.model.CartItemsModel;
import com.example.demo.Cart.model.CartModel;
import com.example.demo.Cart.model.CartProductModel;
import com.example.demo.Cart.repository.CartItemsRepository;
import com.example.demo.Cart.repository.CartProductRepository;
import com.example.demo.Cart.repository.CartRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito; // ใช้ Mockito.mock()
import org.mockito.MockitoAnnotations; // ใช้ Initialize mocks
import org.mockito.Mock; // ยังคงใช้ @Mock, @InjectMocks
import org.mockito.InjectMocks; 

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


// *******************************************************************
// *** ลบ @ExtendWith ออก เพื่อใช้การ Initialize แบบ Manual แทน ***
// *******************************************************************
public class CartServiceTest {

    // 1. ประกาศ Mock Objects (ใช้ @Mock)
    @Mock private CartRepository cartRepository;
    @Mock private CartItemsRepository cartItemsRepository;
    @Mock private CartProductRepository cartProductRepository;

    // 2. Class under Test (ใช้ @InjectMocks)
    @InjectMocks
    private CartService cartService;

    // Global Mock Data
    private final Long USER_ID = 1L;
    private final Long CART_ID = 100L;
    private final Long PRODUCT_ID = 200L;
    private final int AVAILABLE_STOCK = 10;
    private final double PRODUCT_PRICE = 50.0;

    private CartProductModel mockProduct;
    private CartModel mockCart;
    private CartItemsModel mockExistingItem;

    @BeforeEach
    void setUp() {
        // **********************************************
        // บังคับให้ Mockito Initialize mocks ด้วยตัวเอง
        MockitoAnnotations.openMocks(this); 
        // **********************************************
        
        // 1. Product Mock
        mockProduct = new CartProductModel();
        mockProduct.setProductId(PRODUCT_ID);
        mockProduct.setPrice(PRODUCT_PRICE);
        mockProduct.setStockQuantity(AVAILABLE_STOCK);
        mockProduct.setName("Test Product"); // Naming ที่ถูกต้องตาม Entity
        
        // 2. Cart Mock
        mockCart = new CartModel();
        mockCart.setCartId(CART_ID);
        mockCart.setUserId(USER_ID);
        mockCart.setActive(true);
        
        // 3. Existing Cart Item Mock
        mockExistingItem = new CartItemsModel();
        mockExistingItem.setCartItemId(1L);
        mockExistingItem.setCart(mockCart);
        mockExistingItem.setProductId(PRODUCT_ID);
        mockExistingItem.setQuantity(3);
        
        // ** ต้องสร้าง Instance ของ Service ใหม่ทุกครั้ง (ถ้าไม่ใช้ @InjectMocks) แต่ในโค้ดนี้
        // เราใช้ @InjectMocks แล้ว MockitoAnnotations.openMocks(this) จะจัดการ Inject ให้เอง
    }

    // ***************************************************************
    // 1. กรณีที่ไม่มี Cart Active (สร้างใหม่) - (SUCCESS)
    // ***************************************************************
    @Test
    void addProductToCart_NoActiveCart_CreatesNewCartAndItem() {
        // ARRANGE
        int quantity = 3;
        
        when(cartProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
        when(cartRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartModel.class))).thenReturn(mockCart);
        when(cartItemsRepository.findByCart_CartIdAndProductId(CART_ID, PRODUCT_ID)).thenReturn(Optional.empty());
        
        CartItemsModel savedItem = new CartItemsModel();
        savedItem.setProductId(PRODUCT_ID);
        savedItem.setQuantity(quantity);
        when(cartItemsRepository.save(any(CartItemsModel.class))).thenReturn(savedItem);
        
        when(cartRepository.findById(CART_ID)).thenReturn(Optional.of(mockCart));
        when(cartItemsRepository.findByCart_CartId(CART_ID)).thenReturn(List.of(savedItem));

        // ACT
        cartService.addProductToCart(USER_ID, PRODUCT_ID, quantity);

        // ASSERT
        verify(cartRepository, times(1)).save(any(CartModel.class));
        verify(cartItemsRepository, times(1)).save(any(CartItemsModel.class));
    }

    // ***************************************************************
    // 2. กรณีมี Cart Active และมีสินค้าเดิมอยู่แล้ว (เพิ่ม Quantity) - (SUCCESS)
    // ***************************************************************
    @Test
    void addProductToCart_ExistingItem_IncrementsQuantity() {
        // ARRANGE
        int quantityToAdd = 3;
        int expectedNewQuantity = mockExistingItem.getQuantity() + quantityToAdd; // 6

        when(cartProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
        when(cartRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(mockCart));
        when(cartItemsRepository.findByCart_CartIdAndProductId(CART_ID, PRODUCT_ID)).thenReturn(Optional.of(mockExistingItem));
        when(cartItemsRepository.save(any(CartItemsModel.class))).thenReturn(mockExistingItem);

        when(cartRepository.findById(CART_ID)).thenReturn(Optional.of(mockCart));
        when(cartItemsRepository.findByCart_CartId(CART_ID)).thenReturn(List.of(mockExistingItem));

        // ACT
        cartService.addProductToCart(USER_ID, PRODUCT_ID, quantityToAdd);

        // ASSERT
        ArgumentCaptor<CartItemsModel> itemCaptor = ArgumentCaptor.forClass(CartItemsModel.class);
        verify(cartItemsRepository).save(itemCaptor.capture());
        
        assertEquals(expectedNewQuantity, itemCaptor.getValue().getQuantity(), "Quantity should be incremented");
        verify(cartRepository, never()).save(any(CartModel.class));
    }

    // ***************************************************************
    // 3. กรณีมีสินค้าอยู่แล้ว แต่อัปเดตเกินสต็อก (StockException) - (FAILURE)
    // ***************************************************************
    @Test
    void addProductToCart_UpdateExceedsTotalStock_ThrowsStockException() {
        // ARRANGE
        int oldQuantity = 7;
        int quantityToAdd = 4; // รวมเป็น 11 (> 10)
        
        mockExistingItem.setQuantity(oldQuantity); 

        when(cartRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(mockCart));
        when(cartProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
        when(cartItemsRepository.findByCart_CartIdAndProductId(CART_ID, PRODUCT_ID)).thenReturn(Optional.of(mockExistingItem));
        
        // ACT & ASSERT
        assertThrows(StockException.class, 
                     () -> cartService.addProductToCart(USER_ID, PRODUCT_ID, quantityToAdd));
        
        verify(cartItemsRepository, never()).save(any());
        verify(cartRepository, never()).save(any());
    }

    // ***************************************************************
    // 4. กรณีไม่พบ Product (ResourceNotFoundException) - (FAILURE)
    // ***************************************************************
    @Test
    void addProductToCart_ProductNotFound_ThrowsResourceNotFoundException() {
        // ARRANGE
        when(cartProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, 
                     () -> cartService.addProductToCart(USER_ID, PRODUCT_ID, 1));
        
        verify(cartRepository, never()).save(any());
        verify(cartItemsRepository, never()).save(any());
    }
    
    // ***************************************************************
    // 5. กรณี Quantity ที่ส่งมาเป็น 0 หรือติดลบ (IllegalArgumentException) - (FAILURE)
    // ***************************************************************
    @Test
    void addProductToCart_InvalidQuantity_ThrowsIllegalArgumentException() {
        // ARRANGE
        when(cartProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, 
                     () -> cartService.addProductToCart(USER_ID, PRODUCT_ID, 0));
        
        verify(cartRepository, never()).save(any());
        verify(cartItemsRepository, never()).save(any());
    }
}