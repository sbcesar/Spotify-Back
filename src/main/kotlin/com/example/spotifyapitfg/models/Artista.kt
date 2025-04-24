package com.example.spotifyapitfg.models

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Artista(
    @BsonId
    val id: String,
    val nombre: String,
    val imagenUrl: String?,
    val popularidad: Int,
    val seguidores: Int,
    val urlSpotify: String,
    val generos: List<String>
)