package com.fabio.perfumeshop_api.catalog.internal;

import org.springframework.data.jpa.repository.JpaRepository;

 interface PerfumeRepository extends JpaRepository<Perfume, Long> {
}
