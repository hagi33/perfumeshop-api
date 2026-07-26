package com.fabio.perfumeshop_api.user.internal;


import com.fabio.perfumeshop_api.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementa la API pública del módulo user.
 *
 * Igual que PerfumeService implementa CatalogApi: la clase es package-private
 * (invisible desde fuera), pero se expone a través de la interfaz pública que
 * implementa. Otros módulos dependen de UserApi, nunca de esta clase.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserApi {

    private final UserRepository userRepository;

    @Override
    public Optional<Long> findIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId);
    }
}
