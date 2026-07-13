package com.fabio.perfumeshop_api.catalog.internal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfumeRepository extends JpaRepository<Perfume, Long> {
}
