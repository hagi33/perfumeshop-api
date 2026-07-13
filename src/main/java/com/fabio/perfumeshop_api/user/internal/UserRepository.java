package com.fabio.perfumeshop_api.user.internal;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Necesario para el login: buscar un usuario por su email.
    Optional <User> findByEmail(String email);

    boolean existsByEmail(String email);
}
