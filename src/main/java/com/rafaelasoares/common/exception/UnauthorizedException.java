package com.rafaelasoares.common.exception;

/**
 * Falta credencial válida para a operação. Vira <b>401</b>.
 *
 * <p><b>401 não é 403.</b> Aqui o sistema não sabe <i>quem</i> está chamando —
 * o token falta, expirou, foi revogado, ou as credenciais não conferem. Já o
 * 403 ({@code AccessDeniedException}, do Spring Security) é o oposto: sabe-se
 * quem é, e essa pessoa não tem permissão. O nome "Unauthorized" no HTTP é
 * historicamente infeliz — o significado é "não autenticado".
 *
 * <p><b>Cuidado ao criar subclasse:</b> a mensagem não deve revelar qual
 * parte da credencial falhou. Distinguir "e-mail não cadastrado" de "senha
 * errada" permite enumerar quem tem conta no sistema. Ver
 * {@code CredenciaisInvalidasException}, que usa uma mensagem só para os três
 * casos que trata.
 */
public abstract class UnauthorizedException extends DomainException {

    protected UnauthorizedException(String mensagem) {
        super(mensagem);
    }
}
