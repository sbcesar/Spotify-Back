package com.example.spotifyapitfg.service

import com.google.firebase.auth.FirebaseToken
import org.springframework.stereotype.Service

@Service
class FirebaseAuthService {

    /**
     * Verifica el token JWT que envia el cliente (se obtiene desde Firebase)
     * Retorna el token verificado si es valido, si no lanza una excepcion
     */
    fun verifyToken(idToken: String): FirebaseToken {
        return FirebaseAuthService().verifyToken(idToken)
    }

    fun getUidFromToken(idToken: String): String {
        val decodedToken = verifyToken(idToken)
        return decodedToken.uid
    }
}