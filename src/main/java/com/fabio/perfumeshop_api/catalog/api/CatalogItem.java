package com.fabio.perfumeshop_api.catalog.api;

import java.math.BigDecimal;

/**
* Vista pública mínima de un perfume para otros módulos: identidad, precio y
 * stock disponible. No exponemos la entidad JPA ni sus detalles internos
* */


public record CatalogItem(

        Long id,
        String name,
        BigDecimal price,
        int avalibleStock

) {
}
