package com.example.spotifyapitfg.dto

data class UsuarioDTO(
    val id: String? = null,
    val nombre: String,
    val email: String,
    val playlistCount: Int = 0,
    val seguidores: Int = 0,
    val seguidos: Int = 0,
    val biblioteca: BibliotecaDTO? = null,
    val role: String
)