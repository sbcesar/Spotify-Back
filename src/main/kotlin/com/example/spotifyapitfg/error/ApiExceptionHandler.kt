package com.example.spotifyapitfg.error

import com.example.spotifyapitfg.error.exception.ConflictException
import com.example.spotifyapitfg.error.exception.ForbiddenException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ApiExceptionHandler {

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