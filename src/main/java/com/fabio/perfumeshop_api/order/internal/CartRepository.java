package com.fabio.perfumeshop_api.order.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Long> findUserById(Long userId);

}
