package com.example.demo.Order.service;

import com.example.demo.Cart.exception.ResourceNotFoundException;
import com.example.demo.Cart.exception.StockException;
import com.example.demo.Cart.model.CartItemsModel;
import com.example.demo.Cart.model.CartModel;
import com.example.demo.Cart.repository.CartItemsRepository;
import com.example.demo.Cart.repository.CartRepository;
import com.example.demo.Order.DTO.OrderHistoryDTO;
import com.example.demo.Order.DTO.OrderHistoryItemDTO;
import com.example.demo.Order.model.OrderItemModel;
import com.example.demo.Order.model.OrderModel;
import com.example.demo.Order.repository.OrderItemRepository;
import com.example.demo.Order.repository.OrderRepository;
import com.example.demo.Product.model.ProductModel;
import com.example.demo.Product.repository.ProductRepository;
import com.example.demo.auth.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    // แก้ไข: เปลี่ยนชื่อ Repository ให้ตรงกับไฟล์ Model
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean validateUserSession(Long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public OrderModel checkout(Long userId) {
        // 1. โหลด Active Cart ของ User
        CartModel cart = cartRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Active Cart", "User ID", userId));

        List<CartItemsModel> cartItems = cartItemsRepository.findByCart_CartId(cart.getCartId());

        // 2. ตรวจสอบว่ามีสินค้าในตะกร้าหรือไม่
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart.");
        }

        // 3. สร้าง Order ใหม่ และตั้งค่าพื้นฐาน
        OrderModel newOrder = new OrderModel();
        newOrder.setUserID(userId);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus("Pending");

        // บันทึก Order หลักก่อนเพื่อเอา Order ID มาใช้
        OrderModel savedOrder = orderRepository.save(newOrder);

        // แก้ไข: เปลี่ยน List<OrderItemModel> เป็น List<OrderItemsModel>
        List<OrderItemModel> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        // 4. วนลูปเพื่อตรวจสอบ Stock และสร้าง OrderItems
        for (CartItemsModel item : cartItems) {
            ProductModel product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", item.getProductId()));

            // 4a. ตรวจสอบ Stock
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new StockException("Not enough stock for product: " + product.getProductName() +
                        ". Requested: " + item.getQuantity() + ", Available: " + product.getStockQuantity());
            }

            // 4b. สร้าง OrderItem
            // แก้ไข: สร้าง Instance ของ OrderItemsModel
            OrderItemModel orderItem = new OrderItemModel();
            orderItem.setOrder(savedOrder); // เชื่อมกับ Order หลัก
            orderItem.setProductID(product.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getPrice()); // บันทึกราคา ณ ตอนที่สั่งซื้อ
            orderItems.add(orderItem);

            // 4c. คำนวณราคารวม
            totalAmount += item.getQuantity() * product.getPrice();

            // 5. อัปเดต Stock สินค้า
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        // 6. บันทึก OrderItems ทั้งหมด
        orderItemRepository.saveAll(orderItems);

        // 7. อัปเดต TotalAmount และตั้งค่าสถานะสุดท้ายใน Order หลัก
        savedOrder.setTotalAmount(totalAmount);
        // แก้ไข: เปลี่ยนสถานะเป็น "PLACED" ตาม Definition of Done
        savedOrder.setStatus("PLACED");
        orderRepository.save(savedOrder);

        // 8. ลบ CartItems ทั้งหมดของ cart ที่ active แล้ว
        cartItemsRepository.deleteAll(cartItems);

        return savedOrder;
    }

    public List<OrderModel> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserID(userId);
    }

    public List<OrderHistoryDTO> getOrderHistory(Long userId) {
        List<OrderModel> orders = orderRepository.findByUserID(userId);
        List<OrderHistoryDTO> historyList = new ArrayList<>();

        for (OrderModel order : orders) {
            List<OrderHistoryItemDTO> items = getOrderItems(order);

            OrderHistoryDTO dto = new OrderHistoryDTO(
                    order.getOrderID(),
                    order.getOrderDate(),
                    order.getStatus(),
                    items,
                    order.getTotalAmount());

            historyList.add(dto);
        }

        return historyList;
    }

    private List<OrderHistoryItemDTO> getOrderItems(OrderModel order) {
        List<OrderItemModel> orderItemModels = order.getOrderItems();
        List<OrderHistoryItemDTO> itemDTOList = new ArrayList<>();

        for (OrderItemModel item : orderItemModels) {
            String productName = getProductDetail(item.getProductID());

            OrderHistoryItemDTO itemDTO = new OrderHistoryItemDTO(
                    item.getProductID(),
                    productName,
                    item.getUnitPrice(),
                    item.getQuantity());

            itemDTOList.add(itemDTO);
        }

        return itemDTOList;
    }

    private String getProductDetail(Long productId) {
        ProductModel product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));
        return product.getProductName();
    }
}