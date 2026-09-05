package com.rafaelasoares.acesso.exception;

import com.rafaelasoares.common.exception.UnauthorizedException;

/**
 * E-mail não existe, senha não confere, ou usuário inativo.
 *
 * <p>É <b>uma exceção só para os três casos</b>, e a mensagem é sempre a
 * mesma, de propósito: distinguir "e-mail não cadastrado" de "senha errada"
 * permitiria descobrir quem tem conta no sistema (enumeração de usuários).
 */
public class CredenciaisInvalidasException extends UnauthorizedException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }
}
