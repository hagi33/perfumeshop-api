package com.fabio.perfumeshop_api.catalog.internal;


import com.fabio.perfumeshop_api.catalog.api.CatalogApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerfumeService implements CatalogApi {

    private final PerfumeRepository perfumeRepository;
    private final PerfumeMapper perfumeMapper;
    private final ApplicationEventPublisher events;


    @Override
    public Optional<CatalogApi> findById(Long perfumeId) {
        return Optional.empty();
    }

    @Override
    public void decreaseStock(Long perfumeId, int quantity) {

    }
}
