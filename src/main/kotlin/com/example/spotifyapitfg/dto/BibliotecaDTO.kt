package com.example.spotifyapitfg.dto

data class BibliotecaDTO(
    val playlistsCreadas: MutableList<String> = mutableListOf(),
    val likedCanciones: MutableList<String> = mutableListOf(),
    val likedPlaylists: MutableList<String> = mutableListOf(),
    val likedArtistas: MutableList<String> = mutableListOf(),
    val likedAlbums: MutableList<String> = mutableListOf()
)