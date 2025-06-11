package com.example.spotifyapitfg.utils

import com.example.spotifyapitfg.dto.PlaylistCreateDTO
import com.example.spotifyapitfg.dto.PlaylistDTO
import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Biblioteca
import com.example.spotifyapitfg.models.Role
import com.example.spotifyapitfg.models.Usuario

fun crearUsuarioTest(
    id: String = "user123",
    nombre: String = "Test User",
    email: String = "test@example.com",
    biblioteca: Biblioteca = Biblioteca(),
    role: Role = Role.USER
): Usuario {
    return Usuario(
        id = id,
        nombre = nombre,
        email = email,
        playlistCount = 0,
        seguidores = 0,
        seguidos = 0,
        biblioteca = biblioteca,
        role = role
    )
}

fun crearUsuarioDTOTest(
    id: String = "user123",
    nombre: String = "Test User",
    email: String = "test@example.com",
    role: String = "USER"
): UsuarioDTO {
    return UsuarioDTO(
        id = id,
        nombre = nombre,
        email = email,
        playlistCount = 0,
        seguidores = 0,
        seguidos = 0,
        biblioteca = null,
        role = role
    )
}

fun crearPlaylistCreateDTOTest(
    nombre: String = "Playlist Test",
    descripcion: String = "Descripción",
    imagenUrl: String? = null
) = PlaylistCreateDTO(nombre, descripcion, imagenUrl)

fun crearPlaylistDTOTest(
    id: String = "playlist123",
    nombre: String = "Playlist Test"
) = PlaylistDTO(
    id = id,
    nombre = nombre,
    descripcion = "Descripción",
    canciones = emptyList(),
    creadorId = "user123",
    creadorNombre = "Test User",
    imagenUrl = ""
)