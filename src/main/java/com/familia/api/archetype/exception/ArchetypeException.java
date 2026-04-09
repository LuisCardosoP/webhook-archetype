package com.familia.api.archetype.exception;

import lombok.Getter;

/**
 * Excepcion personalizada de ejemplo para el arquetipo de los microservicios. Permite controlar definir excepciones propias de la
 * aplicacion.
 */
@Getter
public class ArchetypeException extends Exception {

    /** Codigo que define el estado de la transaccion */
    private final int status;

    /** Mensaje informativo para el usuario */
    private final String userMessage;

    /** Codigo de error */
    private final String errorCode;

    /**
     * Metodo constructor
     */
    public ArchetypeException(int status, String developerMessage, String userMessage, String errorCode) {
        super(developerMessage);
        this.userMessage = userMessage;
        this.status = status;
        this.errorCode = errorCode;
    }
}