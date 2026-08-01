package com.fabio.perfumeshop_api.order.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);


}
