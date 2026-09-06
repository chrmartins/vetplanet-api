package com.vetplanet.agendamento.service;

import com.vetplanet.agendamento.dto.BloqueioResponseDto;
import com.vetplanet.agendamento.dto.CriarBloqueioRequestDto;
import com.vetplanet.agendamento.entity.BloqueioEntity;
import com.vetplanet.agendamento.exception.BloqueioInvalidoException;
import com.vetplanet.agendamento.repository.BloqueioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria um bloqueio, escolhendo a forma a partir do que veio preenchido.
 *
 * <p>A validação cruzada mora aqui porque é regra, não formato: nenhum campo
 * isolado está errado quando alguém manda dia da semana e período juntos.
 *
 * <p>O banco tem os mesmos check constraints ({@code V005}). Não é
 * duplicação inútil — a checagem daqui existe para devolver uma mensagem que
 * a pessoa entenda, e a do banco para que nenhuma linha torta entre por outro
 * caminho.
 */
@Service
public class CriarBloqueioService {

    private final BloqueioRepository bloqueioRepository;

    public CriarBloqueioService(BloqueioRepository bloqueioRepository) {
        this.bloqueioRepository = bloqueioRepository;
    }

    @Transactional
    public BloqueioResponseDto criarBloqueio(CriarBloqueioRequestDto request) {
        boolean ehSemanal = request.diaDaSemana() != null;
        boolean temPeriodo = request.dataInicio() != null || request.dataFim() != null;

        if (ehSemanal && temPeriodo) {
            throw new BloqueioInvalidoException(
                    "Um bloqueio ou repete toda semana, ou vale para um período — não os dois.");
        }
        if (!ehSemanal && !temPeriodo) {
            throw new BloqueioInvalidoException(
                    "Informe o dia da semana, para repetir, ou as datas de início e fim.");
        }
        if (temPeriodo && (request.dataInicio() == null || request.dataFim() == null)) {
            throw new BloqueioInvalidoException("Informe as datas de início e de fim.");
        }
        if (temPeriodo && request.dataFim().isBefore(request.dataInicio())) {
            throw new BloqueioInvalidoException("A data de fim não pode ser antes da de início.");
        }
        if ((request.horaInicio() == null) != (request.horaFim() == null)) {
            throw new BloqueioInvalidoException(
                    "Informe as duas horas, ou nenhuma para bloquear o dia inteiro.");
        }
        if (request.horaInicio() != null && !request.horaFim().isAfter(request.horaInicio())) {
            throw new BloqueioInvalidoException("A hora de fim precisa ser depois da de início.");
        }
        // Bloqueio semanal de dia inteiro é legítimo: é o "não atendo domingo".

        BloqueioEntity bloqueio =
                ehSemanal
                        ? BloqueioEntity.semanal(
                                request.motivo(),
                                request.diaDaSemana(),
                                request.horaInicio(),
                                request.horaFim())
                        : BloqueioEntity.periodo(
                                request.motivo(),
                                request.dataInicio(),
                                request.dataFim(),
                                request.horaInicio(),
                                request.horaFim());

        return BloqueioResponseDto.de(bloqueioRepository.save(bloqueio));
    }
}
