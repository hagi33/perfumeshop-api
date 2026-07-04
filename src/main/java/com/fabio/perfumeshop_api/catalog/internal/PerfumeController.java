package com.fabio.perfumeshop_api.catalog.internal;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController //Le dice a Spring que esta  clase maneja peticiones HTTP y que
//devuelvan sus métodos va directo al cuerpo de la respuesta como JSON
@RequestMapping("/api/perfumes")//Prefijo común de todas las URLs de este controller
@RequiredArgsConstructor //La ponemos siempre que requeramos inyectar dependencias
class PerfumeController {

    private final PerfumeService perfumeService;


    @GetMapping
    List<PerfumeResponse> getAll(){
        return perfumeService.findAll();
    }

    @GetMapping("/{id}")
    PerfumeResponse getById(@PathVariable Long id){
        return perfumeService.getById(id);
    }

    @PostMapping
    ResponseEntity<PerfumeResponse> create(@Valid @RequestBody CreatePerfumeRequest request){
        PerfumeResponse created = perfumeService.create(request);
        return ResponseEntity
                .created(URI.create("/api/perfumes/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    PerfumeResponse update(@PathVariable Long id,
                           @Valid @RequestBody CreatePerfumeRequest request) {
        return perfumeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delet(@PathVariable Long id){
    perfumeService.delete(id);
    }




}
