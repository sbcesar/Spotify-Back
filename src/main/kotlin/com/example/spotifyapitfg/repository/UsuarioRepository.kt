package com.example.spotifyapitfg.repository

import com.example.spotifyapitfg.models.Usuario
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UsuarioRepository : MongoRepository<Usuario, String> {

    fun findByEmail(email: String): Optional<Usuario>

}