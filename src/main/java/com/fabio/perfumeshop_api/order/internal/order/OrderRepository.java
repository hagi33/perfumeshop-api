package com.fabio.perfumeshop_api.order.internal.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);


}
