package com.flamecommerce.exception;

/** Violação de uma regra de negócio (RN1..RN5). A mensagem é exibida ao usuário. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
