package com.fabio.perfumeshop_api.catalog.internal;


import com.fabio.perfumeshop_api.catalog.api.CatalogApi;
import com.fabio.perfumeshop_api.catalog.api.CatalogItem;
import com.fabio.perfumeshop_api.catalog.api.StockDecreasedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

//En esta clase va la lógica de negocio.(O en la entidad).


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerfumeService implements CatalogApi {

    private final PerfumeRepository perfumeRepository;
    private final PerfumeMapper perfumeMapper;
    private final ApplicationEventPublisher events;


    @Override
    //Usamos Optional porque el perfume que pedimos puede estar o no presente.
    public Optional<CatalogItem> findById(Long perfumeId) {
        return perfumeRepository.findById(perfumeId)
                .map(perfumeMapper::toCatalogItem);
    }


    /**
     * Con este método le pedimos a la entidad que reste la cantidad al stock,
     * el service solo se encarga de pedirle a la entidad que ejecute su método
     * y publica el evento.
     * */
    @Override
    @Transactional
    public void decreaseStock(Long perfumeId, int quantity) {
        Perfume perfume = getOrThrow(perfumeId);
        perfume.decreaseStock(quantity);

        events.publishEvent(new StockDecreasedEvent(
                perfumeId, quantity, perfume.getStock()
        ));
    }

    //--Operaciones HTTP para el controller--

    List<PerfumeResponse> findAll(){

        return perfumeRepository.findAll().stream()
                .map(perfumeMapper::toResponse).toList();

    }

    PerfumeResponse getById(Long id){
        return perfumeMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    PerfumeResponse create(CreatePerfumeRequest request){
        Perfume perfume = perfumeMapper.toEntity(request);
        return perfumeMapper.toResponse(perfumeRepository.save(perfume));

    }

    @Transactional
    PerfumeResponse update(Long id, CreatePerfumeRequest request){
        Perfume perfume = getOrThrow(id);
        perfumeMapper.updateEntity(perfume, request);
        return perfumeMapper.toResponse(perfume);


    }

    @Transactional
    void delete(Long id){
        perfumeRepository.delete(getOrThrow(id));
    }




    //Este método centraliza el buscar o fallar para no tener que repetirlo en los demás métodos
    private Perfume getOrThrow(Long id){
        return perfumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                   "Perfume con id " + id + " no encontrado"
                ));

    }

}
