package com.example.spotifyapitfg.mapper

import com.example.spotifyapitfg.dto.PlaylistDTO
import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Playlist
import com.example.spotifyapitfg.models.Usuario
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")  // Esto hace que el MapStruct se integre como Bean
interface Mapper {

    // Para usuario
    fun toDTO(usuario: Usuario): UsuarioDTO

    fun toEntity(usuarioDTO: UsuarioDTO): Usuario

    // Para playlist
    fun toDTO(playlist: Playlist): PlaylistDTO


}