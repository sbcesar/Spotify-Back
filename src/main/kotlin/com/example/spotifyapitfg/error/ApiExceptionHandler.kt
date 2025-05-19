package com.example.spotifyapitfg.error

import com.example.spotifyapitfg.error.exception.ConflictException
import com.example.spotifyapitfg.error.exception.ForbiddenException
import com.google.firebase.auth.AuthErrorCode
import com.google.firebase.auth.FirebaseAuthException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(FirebaseAuthException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun handleFirebaseAuthException(request: HttpServletRequest, e: FirebaseAuthException): ErrorRespuesta {
        val errorMessage = when (e.authErrorCode) {
            AuthErrorCode.EMAIL_ALREADY_EXISTS -> "El correo electronico ya esta registrado."
            AuthErrorCode.EMAIL_NOT_FOUND -> "El correo electronico no es valido."
            else -> e.message ?: "Error desconocido de la autenticacion."
        }

        return ErrorRespuesta(errorMessage, request.requestURI)
    }

    // Cuando todavia no esta validado el usuario (login)
    @ExceptionHandler(UsernameNotFoundException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    fun handleUsernameNotFoundException(request: HttpServletRequest, e: Exception): ErrorRespuesta {
        return ErrorRespuesta(e.message ?: "Usuario no encontrado", request.requestURI)
    }

    @ExceptionHandler(Exception::class, NullPointerException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGenericException(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    fun handleForbiddenException(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun handleConflictException(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        return ErrorRespuesta(e.message!!, request.requestURI)
    }


}