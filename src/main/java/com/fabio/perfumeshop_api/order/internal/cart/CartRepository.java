package com.fabio.perfumeshop_api.order.internal.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {


    /*
    * findByUserId con LEFT JOIN FETCH c.items. El JOIN FETCH trae el carrito y sus líneas en una sola consulta,
    * evitando el problema N+1 y el LazyInitializationException al recorrer los items fuera de la sesión.
    * */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") Long userId);
}
