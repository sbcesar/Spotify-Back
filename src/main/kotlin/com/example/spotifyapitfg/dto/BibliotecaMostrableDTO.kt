package com.example.spotifyapitfg.dto

import com.example.spotifyapitfg.models.Album
import com.example.spotifyapitfg.models.Artista
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.models.Playlist

data class BibliotecaMostrableDTO(
    val canciones: List<Cancion>,
    val playlists: List<Playlist>,
    val artistas: List<Artista>,
    val albumes: List<Album>
)