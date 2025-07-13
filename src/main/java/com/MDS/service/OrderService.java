package com.MDS.service;

import com.MDS.entity.Order;

import java.util.List;

public interface OrderService {
    Order saveOrder(Order order);
    List<Order> getAllOrders();
}
