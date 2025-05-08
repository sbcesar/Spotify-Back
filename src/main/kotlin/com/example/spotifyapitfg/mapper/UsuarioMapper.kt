package com.example.spotifyapitfg.mapper

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Usuario
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")  // Esto hace que el MapStruct se integre como Bean
interface UsuarioMapper {

    fun toDTO(usuario: Usuario): UsuarioDTO

    fun toEntity(usuarioDTO: UsuarioDTO): Usuario

//    fun toDTO(usuario: Usuario): UsuarioDTO {
//        return UsuarioDTO(
//            id = usuario.id,
//            nombre = usuario.nombre,
//            email = usuario.email,
//            playlistCount = usuario.playlistCount,
//            seguidores = usuario.seguidores,
//            seguidos = usuario.seguidos,
//            biblioteca = BibliotecaDTO(
//                playlistsCreadas = usuario.biblioteca.playlistsCreadas,
//                likedCanciones = usuario.biblioteca.likedCanciones,
//                likedPlaylists = usuario.biblioteca.likedPlaylists,
//                likedArtistas = usuario.biblioteca.likedArtistas
//            )
//        )
//    }

//    fun fromDTO(usuarioDTO: UsuarioDTO, password: String): Usuario {
//        return Usuario(
//            id = usuarioDTO.id,
//            nombre = usuarioDTO.nombre,
//            email = usuarioDTO.email,
//            password = password,
//            playlistCount = usuarioDTO.playlistCount,
//            seguidores = usuarioDTO.seguidores,
//            seguidos = usuarioDTO.seguidos,
//            biblioteca = Biblioteca(
//                playlistsCreadas = usuarioDTO.biblioteca.playlistsCreadas,
//                likedCanciones = usuarioDTO.biblioteca.likedCanciones,
//                likedPlaylists = usuarioDTO.biblioteca.likedPlaylists,
//                likedArtistas = usuarioDTO.biblioteca.likedArtistas
//            )
//        )
//    }
}