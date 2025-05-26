package com.example.spotifyapitfg.dto

data class UsuarioBibliotecaMostrableDTO (
    val id: String,
    val nombre: String,
    val email: String,
    val playlistCount: Int,
    val seguidores: Int,
    val seguidos: Int,
    val biblioteca: BibliotecaMostrableDTO
)