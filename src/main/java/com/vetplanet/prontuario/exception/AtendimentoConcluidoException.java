package com.vetplanet.prontuario.exception;

import com.vetplanet.common.exception.BusinessRuleException;

/**
 * Tentou alterar um atendimento já fechado.
 *
 * <p>Prontuário concluído não se reescreve: corrige-se por retificação, que é
 * registro novo com data, autor e motivo, sem tocar no original. É isso que
 * faz o documento valer — quem lê vê o que foi escrito na hora e o que foi
 * corrigido depois.
 */
public class AtendimentoConcluidoException extends BusinessRuleException {

    public AtendimentoConcluidoException() {
        super(
                "Este atendimento já foi concluído e não pode ser alterado."
                        + " Para corrigir, registre uma retificação.");
    }
}
