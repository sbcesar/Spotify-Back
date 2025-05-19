package com.example.spotifyapitfg.security

import org.springframework.security.config.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(private val firebaseFilter: FirebaseAuthenticationFilter) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth -> auth
                // Poner todos los permisos
                .requestMatchers("/usuario/register").permitAll()
                .anyRequest().authenticated()
            }

            .sessionManagement{ session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(firebaseFilter, UsernamePasswordAuthenticationFilter::class.java)


        return http.build()
    }

}