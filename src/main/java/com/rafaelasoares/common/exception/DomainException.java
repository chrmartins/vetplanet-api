package com.rafaelasoares.common.exception;

/**
 * Raiz de toda exceção de regra de negócio do sistema.
 *
 * <p>Existe para que o tratamento HTTP seja por <b>categoria</b> e não por
 * classe: o {@code ApiExceptionHandler} lida com as quatro subclasses diretas
 * ({@link UnauthorizedException}, {@link NotFoundException},
 * {@link ConflictException}, {@link BusinessRuleException}) e, com isso, cobre
 * todas as exceções que os domínios criarem daqui pra frente sem ganhar um
 * método novo a cada uma.
 *
 * <p>Cada domínio cria as suas em {@code <dominio>/exception/}, com nome que
 * descreve a situação em português — {@code UsuarioNaoEncontradoException},
 * {@code HorarioIndisponivelException} — estendendo a categoria certa.
 *
 * <p><b>Não estenda esta classe diretamente: escolha uma das quatro
 * categorias.</b> Se nenhuma servir, o certo é acrescentar uma quinta aqui —
 * não furar a abstração estendendo a raiz e pedir um método dedicado no
 * handler, porque aí ele volta a crescer a cada domínio.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String mensagem) {
        super(mensagem);
    }

    protected DomainException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
