package com.example.spotifyapitfg.dto

import com.example.spotifyapitfg.models.Cancion

data class PlaylistDTO(
    val id: String,
    val nombre: String,
    val descripcion: String?,
    val canciones: List<Cancion>,
    val creadorId: String,
    val creadorNombre: String,
    val imagenUrl: String? = null
)
