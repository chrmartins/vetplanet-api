package com.vetplanet.cliente.service;

import java.util.UUID;

/**
 * O animal tem algo preso a ele?
 *
 * <p><b>A interface é declarada aqui, em `cliente`, e implementada lá fora.</b>
 * É inversão de dependência de propósito: `agendamento` já depende de
 * `cliente` (precisa do animal para marcar consulta), e se `cliente`
 * importasse `agendamento` de volta teríamos um ciclo entre módulos — o
 * primeiro sinal de que a fronteira está furada.
 *
 * <p>Com a interface deste lado, `cliente` pergunta sem conhecer quem
 * responde. `prontuario` entra como segunda implementação quando existir, sem
 * tocar em nada aqui.
 *
 * <p>Serve só para a **aparência** da tela — esconder a lixeira quando o
 * delete fosse falhar. Quem de fato impede é a chave estrangeira no banco.
 */
public interface HistoricoDoAnimal {

    boolean temHistorico(UUID idAnimal);
}
