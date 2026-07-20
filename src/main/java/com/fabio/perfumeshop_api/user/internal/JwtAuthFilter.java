package com.fabio.perfumeshop_api.user.internal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException{

        // Sacamos la cabecera Authorization.
        String authHeader = request.getHeader("Authorization");

        // Si no hay cabecera o no empieza por Bearer, seguimos sin autenticar,
        //(Puede ser un endpoint público, la config de seguridad decidirá)
        if (authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        //Extraemos el Bearer del principio del token.
        String token = authHeader.substring(7);


        //Si el token es válido y aún no hay nadie autenticado en esta petición
        if (jwtService.isValid(token) &&
                SecurityContextHolder.getContext().getAuthentication() == null){

            String email = jwtService.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            //Creamos el objeto de autenticación y lo metemos en el contexto de Spring.
            //A partir de aquí Spring sabe quién es el usuario y qué rol tiene.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);



        }

        // Continuamos con la cadena de filtros.
        filterChain.doFilter(request, response);

    }



}
