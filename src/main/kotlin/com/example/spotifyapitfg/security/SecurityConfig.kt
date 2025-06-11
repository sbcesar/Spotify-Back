package com.example.spotifyapitfg.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
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

                .requestMatchers("/usuario/register").permitAll()
                .requestMatchers("/spotify/token").permitAll()

                .requestMatchers(HttpMethod.GET, "/usuario/perfil").permitAll()

                .requestMatchers(HttpMethod.GET, "/spotify/buscar/canciones").permitAll()
                .requestMatchers(HttpMethod.GET, "/spotify/buscar/albumes").permitAll()
                .requestMatchers(HttpMethod.GET, "/spotify/buscar/artistas").permitAll()
                .requestMatchers(HttpMethod.GET, "/spotify/buscar/playlists").permitAll()

                .requestMatchers(HttpMethod.GET, "/canciones/all").permitAll()
                .requestMatchers(HttpMethod.POST, "/canciones/like/{cancionId}").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/canciones/like/{cancionId}").permitAll()

                .requestMatchers(HttpMethod.POST, "/artistas/like/{artistaId}").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/artistas/like/{artistaId}").permitAll()

                .requestMatchers(HttpMethod.POST, "/playlists/like/{playlistId}").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/playlists/like/{playlistId}").permitAll()
                .requestMatchers(HttpMethod.POST, "/playlists/crear").authenticated()
                .requestMatchers(HttpMethod.GET, "/playlists/creadas").authenticated()
                .requestMatchers(HttpMethod.GET, "/playlists/todas").permitAll()
                .requestMatchers(HttpMethod.PUT, "/playlists/{playlistId}/editar").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/playlists/{playlistId}").authenticated()
                .requestMatchers(HttpMethod.POST, "/playlists/mix").hasAnyRole("PREMIUM", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/playlists/{playlistId}/agregarCancion/{cancionId}").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/playlists/{playlistId}/eliminarCancion/{cancionId}").authenticated()

                .requestMatchers(HttpMethod.POST, "/albumes/like/{albumId}").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/albumes/like/{albumId}").permitAll()

                .anyRequest().authenticated()
            }

            .sessionManagement{ session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(firebaseFilter, UsernamePasswordAuthenticationFilter::class.java)


        return http.build()
    }

}