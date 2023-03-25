package ru.quipy.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class RestResponseEntityExceptionHandler {

    @ExceptionHandler(Throwable::class)
    fun handleException(ex: Throwable) = ResponseEntity<String>(
        ex.message, HttpStatus.BAD_REQUEST
    )
}