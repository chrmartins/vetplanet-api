package com.vetplanet.web;

import com.vetplanet.common.exception.BusinessRuleException;
import com.vetplanet.common.exception.ConflictException;
import com.vetplanet.common.exception.NotFoundException;
import com.vetplanet.common.exception.UnauthorizedException;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// Jackson 3 mudou de pacote: `com.fasterxml.jackson` virou `tools.jackson`.
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

/**
 * Traduz exceção de domínio em resposta HTTP.
 *
 * <p>O tratamento é por <b>categoria</b> ({@code UnauthorizedException},
 * {@code NotFoundException}, {@code ConflictException},
 * {@code BusinessRuleException}), não por classe concreta. Assim, quando
 * `cliente` criar {@code AnimalNaoEncontradoException} ou `agendamento` criar
 * {@code HorarioIndisponivelException}, elas já são tratadas — basta estender
 * a categoria certa. Este arquivo não cresce junto com o sistema.
 *
 * <p><b>Esta classe não deve importar nada de um domínio.</b> Um
 * {@code @ExceptionHandler} apontando para exceção concreta de {@code acesso}
 * ou {@code agendamento} é o sinal de que falta uma categoria em
 * {@code common.exception} — crie a categoria, não o método.
 *
 * <p>Todo erro é logado. Não é preciso escrever o id da requisição na
 * mensagem: ele está no MDC e o padrão de log o imprime em toda linha (ver
 * {@link RequestIdFilter}).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * Falta credencial válida → 401.
     *
     * <p>A mensagem vem pronta da exceção e é deliberadamente vaga: no login,
     * diferenciar "e-mail não cadastrado" de "senha errada" permitiria
     * descobrir quem tem conta.
     *
     * <p>Logar é de propósito — tentativa de autenticação que falha é evento
     * de segurança, e o id da requisição no MDC liga esta linha ao resto do
     * rastro. A mensagem registrada é a genérica, então não vai e-mail nenhum
     * para o log.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> tratarNaoAutenticado(UnauthorizedException erro) {
        log.info("Falha de autenticação: {}", erro.getMessage());
        return resposta(HttpStatus.UNAUTHORIZED, erro.getMessage());
    }

    /**
     * Autenticado, mas sem permissão → 403.
     *
     * <p>É o caso de um ATENDENTE tentando gerenciar usuários.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> tratarAcessoNegado(AccessDeniedException erro) {
        log.info("Acesso negado: {}", erro.getMessage());
        return resposta(
                HttpStatus.FORBIDDEN, "Você não tem permissão para executar esta operação.");
    }

    /** Recurso inexistente → 404. */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> tratarNaoEncontrado(NotFoundException erro) {
        // Nível info: é desfecho esperado de negócio, não defeito do sistema.
        log.info("Recurso não encontrado: {}", erro.getMessage());
        return resposta(HttpStatus.NOT_FOUND, erro.getMessage());
    }

    /** Conflito com dado existente (unicidade, concorrência) → 409. */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> tratarConflito(ConflictException erro) {
        log.info("Conflito: {}", erro.getMessage());
        return resposta(HttpStatus.CONFLICT, erro.getMessage());
    }

    /** Requisição bem formada, mas que fere regra de negócio → 422. */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> tratarRegraDeNegocio(BusinessRuleException erro) {
        log.info("Regra de negócio violada: {}", erro.getMessage());
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, erro.getMessage());
    }

    /** Falha do Bean Validation nos DTOs de entrada → 400, campo a campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> tratarValidacao(MethodArgumentNotValidException erro) {
        List<ErrorResponse.CampoInvalido> campos =
                erro.getBindingResult().getFieldErrors().stream()
                        .map(
                                campo ->
                                        new ErrorResponse.CampoInvalido(
                                                campo.getField(), campo.getDefaultMessage()))
                        .toList();

        log.debug("Requisição inválida: {}", campos);

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.deValidacao(
                                HttpStatus.BAD_REQUEST.value(),
                                "Há campos inválidos na requisição.",
                                campos));
    }

    /**
     * Corpo que o Jackson não consegue ler → 400.
     *
     * <p>O caso comum é valor fora de um enum: mandar {@code "COELHO"} em
     * {@code especie} não chega ao Bean Validation, porque a desserialização
     * falha antes. Sem este handler isso caía na rede de segurança final e
     * virava **500 com "informe o identificador ao suporte"** — culpando o
     * servidor por um erro de quem chamou, e escondendo do cliente o que ele
     * precisa corrigir. Vale para todos os enums da API
     * ({@code PerfilAcesso}, {@code EspecieAnimal}, {@code SexoAnimal}...).
     *
     * <p>A mensagem devolvida é própria, e não a do Jackson: a original traz
     * nome de classe e pacote, que é detalhe interno.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> tratarCorpoIlegivel(HttpMessageNotReadableException erro) {
        log.debug("Corpo da requisição ilegível: {}", erro.getMessage());

        if (erro.getCause() instanceof InvalidFormatException formato) {
            String campo = caminhoDoCampo(formato);
            String valoresAceitos = valoresAceitos(formato);
            String mensagem =
                    valoresAceitos == null
                            ? "Valor inválido para este campo."
                            : "Valor inválido. Use um destes: " + valoresAceitos + ".";

            return ResponseEntity.badRequest()
                    .body(
                            ErrorResponse.deValidacao(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "Há campos inválidos na requisição.",
                                    List.of(new ErrorResponse.CampoInvalido(campo, mensagem))));
        }

        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou malformado.");
    }

    /**
     * `animais[0].especie` — mesmo formato que o Bean Validation usa, para a
     * tela tratar os dois tipos de erro de campo do mesmo jeito.
     *
     * <p>No Jackson 3 o acessor é {@code getPropertyName()}; era
     * {@code getFieldName()} na série 2.
     */
    private static String caminhoDoCampo(InvalidFormatException erro) {
        StringBuilder caminho = new StringBuilder();
        for (JacksonException.Reference referencia : erro.getPath()) {
            if (referencia.getPropertyName() != null) {
                if (!caminho.isEmpty()) caminho.append('.');
                caminho.append(referencia.getPropertyName());
            } else {
                caminho.append('[').append(referencia.getIndex()).append(']');
            }
        }
        return caminho.isEmpty() ? "(corpo)" : caminho.toString();
    }

    /** Lista as constantes quando o alvo é enum; null para os demais tipos. */
    private static String valoresAceitos(InvalidFormatException erro) {
        Class<?> alvo = erro.getTargetType();
        if (alvo == null || !alvo.isEnum()) return null;
        return Arrays.stream(alvo.getEnumConstants()).map(Object::toString).collect(joining(", "));
    }

    /**
     * Rede de segurança para a corrida entre dois cadastros com o mesmo dado
     * único: a checagem prévia no service pode passar nos dois, e aí quem
     * barra é a constraint do banco.
     *
     * <p>A mensagem devolvida é genérica de propósito — nome de constraint
     * expõe estrutura interna do banco. O detalhe fica no log.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> tratarViolacaoDeIntegridade(
            DataIntegrityViolationException erro) {
        log.warn("Violação de integridade no banco", erro);
        return resposta(
                HttpStatus.CONFLICT, "A operação viola uma restrição de integridade dos dados.");
    }

    /**
     * Rede de segurança final: qualquer coisa não prevista vira 500 genérico.
     *
     * <p>Sem isto, um defeito de programação (um {@code NullPointerException},
     * por exemplo) devolveria stack trace ao cliente, entregando detalhe
     * interno do sistema. Aqui o cliente recebe só o id da requisição — e é
     * com ele que se acha o stack trace completo no log.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> tratarErroInesperado(Exception erro) {
        log.error("Erro inesperado ao processar a requisição", erro);
        return resposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro inesperado. Informe o identificador da requisição ao suporte.");
    }

    private ResponseEntity<ErrorResponse> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(ErrorResponse.de(status.value(), mensagem));
    }
}
