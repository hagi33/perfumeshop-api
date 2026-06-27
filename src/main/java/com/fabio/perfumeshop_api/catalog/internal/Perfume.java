package com.fabio.perfumeshop_api.catalog.internal;


import com.fabio.perfumeshop_api.catalog.api.InsufficientStockException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;



/**
 * Entidad de persistencia de un perfume. package-private: ninguna clase fuera
 * de catalog.internal puede importarla. Es la barrera física que el compilador
 * impone, complementaria a la verificación de Spring Modulith.
 *
 * La lógica de stock vive aquí, pegada al dato que protege: un Perfume no
 * puede quedar con stock negativo, lo manipule quien lo manipule.
* */

@Entity
@Table(name = "Perfumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perfume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //IDENTITY para que el atributo sea autoincremental en la DB
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(length = 1000, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OlfactoryFamily family;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cocentration concentration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "volume_ml", nullable = false)
    private Integer volumeML;//Utilizamos Integer en volume porque el volume no puede ser 0, como mucho null

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    private String imageURL;

    /**
     * Descuenta unidades protegiendo la invariante de stock no negativo.
     * Tenerlo en la entidad es information hiding: la regla vive junto al dato.
     */

    void decreaseStock(int quantity){
        if (quantity > this.stock){
            throw new InsufficientStockException(this.id, quantity, this.stock);
        }
        this.stock -= quantity;


    }




}
