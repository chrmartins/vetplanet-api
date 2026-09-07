package com.vetplanet.prontuario.service;

import com.vetplanet.prontuario.dto.AtendimentoResponseDto;
import com.vetplanet.prontuario.dto.RegistrarAtendimentoRequestDto;
import com.vetplanet.prontuario.entity.AtendimentoEntity;
import com.vetplanet.prontuario.exception.AtendimentoConcluidoException;
import com.vetplanet.prontuario.exception.AtendimentoNaoEncontradoException;
import com.vetplanet.prontuario.repository.AtendimentoRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Salva o conteúdo clínico do rascunho.
 *
 * <p>Chamado a cada salvamento automático enquanto ela escreve, e por isso
 * aceita o registro pela metade. Quem exige completude é a conclusão.
 *
 * <p><b>Recusa mexer em atendimento concluído.</b> É a trava que dá valor ao
 * documento: fechado, só se corrige por retificação — registro novo, com data,
 * autor e motivo, sem tocar no original.
 */
@Service
public class RegistrarAtendimentoService {

    private final AtendimentoRepository atendimentoRepository;

    public RegistrarAtendimentoService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    @Transactional
    public AtendimentoResponseDto registrar(
            UUID idAtendimento, RegistrarAtendimentoRequestDto request) {
        AtendimentoEntity atendimento =
                atendimentoRepository
                        .findById(idAtendimento)
                        .orElseThrow(() -> new AtendimentoNaoEncontradoException(idAtendimento));

        if (atendimento.estaConcluido()) throw new AtendimentoConcluidoException();

        atendimento.registrar(
                request.localAtendimento(),
                request.anamnese(),
                request.exameClinico(),
                request.diagnosticoPresuntivo(),
                request.diagnosticoConclusivo(),
                request.recomendacoes(),
                request.pesoKg(),
                request.temperaturaC(),
                request.frequenciaCardiaca(),
                request.frequenciaRespiratoria());

        return AtendimentoResponseDto.de(atendimento);
    }
}
