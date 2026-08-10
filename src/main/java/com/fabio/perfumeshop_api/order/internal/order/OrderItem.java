package com.fabio.perfumeshop_api.order.internal.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Referencia al perfume, sin relación JPA como en los demás módulo
    @Column(name = "perfume_id", nullable = false)
    private Long perfumeId;

    // Nombre del perfume en el momento de la compra
    @Column(name = "perfume_name", nullable = false)
    private String perfumeName;

    //El precio unitario en el momento de la compra, no el precio actual
    @Column(name = "unit_price", nullable = false, precision = 6, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

}
