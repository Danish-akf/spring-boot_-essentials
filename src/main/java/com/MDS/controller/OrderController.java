package com.MDS.controller;

import com.MDS.entity.Order;
import com.MDS.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // Save order
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        Order savedOrder = orderRepository.save(order);
        if (savedOrder.getId() != null) {
            return ResponseEntity.ok("Order saved successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save order.");
        }
    }
    // Get all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
