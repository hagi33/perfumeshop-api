package com.fabio.perfumeshop_api;

import com.fabio.perfumeshop_api.catalog.internal.*;
import com.fabio.perfumeshop_api.shared.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;


import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;





@ExtendWith(MockitoExtension.class) // Esta anotación activa Mockito: hace que @Mock e @InjectMocks funcionen.
public class PerfumeServiceTest {

    // Mocks: versiones falsas de las dependencias del service.
    @Mock
    private  PerfumeRepository perfumeRepository;

    @Mock
    private  PerfumeMapper perfumeMapper;

    // El service real, pero con los mocks de arriba inyectados.
    @InjectMocks
    private  PerfumeService perfumeService;


    @Test
    void findById_whenExists_returnPerfume(){
        //ARRANGE: preparamos el escenario
        // Creamos un perfume de ejemplo que el mock devolverá.
        Perfume perfume = Perfume.builder()
                .id(1L)
                .name("Aventus")
                .brand("Creed")
                .price(new BigDecimal("320.00"))
                .stock(8)
                .build();

        // Le decimos al mock del repositorio qué devolver cuando le pidan el id 1.
        when(perfumeRepository.findById(1L)).thenReturn(Optional.of(perfume));

        // Y le decimos al mock del MAPPER qué devolver cuando convierta ese perfume.
        PerfumeResponse responseEsperado = new PerfumeResponse(
                1L, "Aventus", "Creed", null, null, null, null, null,
                new BigDecimal("320.00"), 8, null);

        when(perfumeMapper.toResponse(perfume)).thenReturn(responseEsperado);
        //ACT: ejecutamos el método que queremos probar.
        PerfumeResponse resultado = perfumeService.getById(1L);

        //ASSERT: comprobamos que el resultado es el esperado.
        assertThat(resultado).isNotNull();
        assertThat(resultado.name()).isEqualTo("Aventus");

        //Verificamos que el service llamó al repositorio como se espera.
        verify(perfumeRepository).findById(1L);
    }


    @Test
    void findById_When_NotExists_throwsException(){
        // ARRANGE: el mock devuelve vacío (no encontrado).
        when(perfumeRepository.findById(99L)).thenReturn(Optional.empty());


        //ACT + ASSERT: comprobamos que lanza la excepción correcta
        assertThatThrownBy(() -> perfumeService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");


    }





    /*Response
    * (
        Long id, String name, String brand,
        String description, OlfactoryFamily family,
        Concentration concentration, Gender gender,
        Integer volumeMl, BigDecimal price, int stock,
        String imageUrl)
    *
    * */



}
