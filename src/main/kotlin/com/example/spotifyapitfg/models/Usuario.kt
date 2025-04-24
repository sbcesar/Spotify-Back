package com.example.spotifyapitfg.models

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Usuario(
    @BsonId
    val id: String?,
    val nombre: String,
    val email: String,
    val password: String,
    val playlistCount: Int = 0,
    val seguidores: Int = 0,
    val seguidos: Int = 0,

    val playlistsCreadas: MutableList<String> = mutableListOf(),
    val likedCanciones: MutableList<String>,
    val likedPlaylists: MutableList<String>,
    val likedArtistas: MutableList<String>,
    val role: Role? = Role.USER
)