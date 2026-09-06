package com.vetplanet.agendamento.dto;

import com.vetplanet.agendamento.entity.BloqueioEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Bloqueio como sai da API.
 *
 * <p>Sai <b>como foi guardado</b>: a regra, não as ocorrências dela. Expandir
 * um almoço semanal nos 30 dias do mês exibido seria trabalho do backend
 * chutando qual período interessa ao cliente — e a tela, que já sabe que dia
 * está desenhando, resolve com uma comparação de dia da semana.
 */
public record BloqueioResponseDto(
        UUID idBloqueio,
        String motivo,
        Short diaDaSemana,
        LocalDate dataInicio,
        LocalDate dataFim,
        LocalTime horaInicio,
        LocalTime horaFim,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm) {

    public static BloqueioResponseDto de(BloqueioEntity bloqueio) {
        return new BloqueioResponseDto(
                bloqueio.getId(),
                bloqueio.getMotivo(),
                bloqueio.getDiaDaSemana(),
                bloqueio.getDataInicio(),
                bloqueio.getDataFim(),
                bloqueio.getHoraInicio(),
                bloqueio.getHoraFim(),
                bloqueio.getCriadoEm(),
                bloqueio.getAtualizadoEm());
    }
}
