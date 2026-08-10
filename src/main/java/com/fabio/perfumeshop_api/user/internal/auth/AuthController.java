package com.fabio.perfumeshop_api.user.internal.auth;


import com.fabio.perfumeshop_api.user.internal.auth.dto.LoginRequest;
import com.fabio.perfumeshop_api.user.internal.auth.dto.RegisterRequest;
import com.fabio.perfumeshop_api.user.internal.auth.dto.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    void register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);

    }


    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);


    }

}
