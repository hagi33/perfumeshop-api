package com.fabio.perfumeshop_api.catalog.api;


import java.util.List;
import java.util.Optional;
import com.fabio.perfumeshop_api.catalog.internal.CatalogItemRecommendation;

/**
 * Punto de entrada del módulo catalog para el resto de la aplicación.
 * Expone solo lo que otros módulos van a necesitar, lo demás queda oculto,
 * */
public interface CatalogApi {

    /**
     * Datos públicos de un perfume, o vacío si no existe.
     * Devuelve Optional en vez de lanzar porque "no existe" es un resultado normal
     * para quien consulta, no un error
     * */
    Optional<CatalogItem> findById(Long perfumeId);


    /**
     * Descuenta unidades al confirmarse un pedido.
     * El módulo que llama no comprueba el stock, el módulo decide
     * */
    void decreaseStock(Long perfumeId, int quantity);


    /*
    * Lista completa de perfumes con datos ricos para recomendación (chat).
    * Devuelve nombre, marca, familia y notas de cada perfume.
    * */
    List<CatalogItemRecommendation> findAllForRecommendation();

}
