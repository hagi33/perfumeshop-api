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
    private final JwtService jwtService;

    /*
    * La forma pública de crear usuarios, no dejamos crear usuarios admin, solo de rol user.
    * */
    @Transactional //La anotación indica que es un método que se ejecutará en la DB.
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

    AuthResponse login(LoginRequest request){
        // Buscamos el usuario por email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        //Comprobamos que la contraseña coincide con el hash guardado
        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new InvalidCredentialsException();
        }

        //Generamos el token
        String token = jwtService.generateToken(user.getEmail(),user.getRole());

        return new AuthResponse(token);
    }



}
