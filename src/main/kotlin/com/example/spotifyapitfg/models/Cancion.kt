package com.example.spotifyapitfg.models

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Cancion(
    @BsonId
    val id: String,
    val nombre: String,
    val artista: String,
    val album: String,
    val imagenUrl: String?,
    val duracionMs: Int,
    val previewUrl: String?,
    val popularidad: Int,
    val urlSpotify: String,
    val audioUrl: String? = null
)