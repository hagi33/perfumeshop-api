package com.fabio.perfumeshop_api.catalog.internal;

import com.fabio.perfumeshop_api.catalog.api.CatalogItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface PerfumeMapper {

    //Con este método mapeamos la entidad al DTO público del paquete api
    @Mapping(target = "availableStock", source = "stock")
    CatalogItem toCatalogItem (Perfume perfume);


    /**
     * Convierte una entidad en el DTO de salid.
     * Se usa cada vez que se devuelve la petición de un perfume al cliente
    * */
    PerfumeResponse toResponse (Perfume perfume);


    /**
     * Convierte el DTO de entrada (createPerfumeRequest) en una entidad nueva.
     * con el (target = "id", ignore = true) le indicamos a mapStruct que no busque el id.
     * De lo contrarío buscaría el id y daría error. (El DTO no tiene id, el usuario no lo especifica en el JSON)
    * */
    @Mapping(target = "id", ignore = true)
    Perfume toEntity(CreatePerfumeRequest request);



    /**
     * Primero, devuelve void, no crea nada nuevo. Segundo, ese @MappingTarget delante del perfume.
     * Lo que hace es: en vez de crear una entidad nueva, modifica una que ya existe.
     * Le pasas el perfume que ya está en la base de datos y el request con los datos nuevos,
     * y MapStruct copia los valores del request encima de la entidad existente.
    * */
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Perfume perfume, CreatePerfumeRequest request);
}
