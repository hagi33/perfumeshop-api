package com.fabio.perfumeshop_api.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
    * La forma pública de crear usuarios, no dejamos crear usuarios admin, solo de rol user.
    * */
    @Transactional
    void register(RegisterRequest request){

        if (userRepository.existsByEmail(request.email())){
                throw new EmailAlreadyExistsException(request.email());


        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }


}
