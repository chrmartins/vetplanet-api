package com.vetplanet.prontuario.service;

import com.vetplanet.agendamento.entity.StatusConsulta;
import com.vetplanet.agendamento.service.AlterarStatusConsultaService;
import com.vetplanet.prontuario.dto.AtendimentoResponseDto;
import com.vetplanet.prontuario.entity.AtendimentoEntity;
import com.vetplanet.prontuario.exception.AtendimentoConcluidoException;
import com.vetplanet.prontuario.exception.AtendimentoIncompletoException;
import com.vetplanet.prontuario.exception.AtendimentoNaoEncontradoException;
import com.vetplanet.prontuario.repository.AtendimentoRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fecha o atendimento — e, com ele, a consulta.
 *
 * <p><b>Concluir o prontuário é o que conclui a consulta.</b> Não existe botão
 * "Concluir" separado na Agenda, e é decisão de produto: na vida real você não
 * termina o atendimento e depois escreve — escrever é terminar. Dois botões
 * dizendo "acabei" permitiriam marcar uma consulta como realizada sem o
 * documento que a lei exige que exista.
 *
 * <p>É o <b>ponto sem volta</b>. Antes daqui o rascunho se descarta à vontade;
 * depois, só retificação. Por isso a tela pergunta "tem certeza?" antes de
 * chamar este serviço.
 */
@Service
public class ConcluirAtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final AlterarStatusConsultaService alterarStatusConsultaService;

    public ConcluirAtendimentoService(
            AtendimentoRepository atendimentoRepository,
            AlterarStatusConsultaService alterarStatusConsultaService) {
        this.atendimentoRepository = atendimentoRepository;
        this.alterarStatusConsultaService = alterarStatusConsultaService;
    }

    @Transactional
    public AtendimentoResponseDto concluir(UUID idAtendimento) {
        AtendimentoEntity atendimento =
                atendimentoRepository
                        .findById(idAtendimento)
                        .orElseThrow(() -> new AtendimentoNaoEncontradoException(idAtendimento));

        if (atendimento.estaConcluido()) throw new AtendimentoConcluidoException();

        // O banco tem o mesmo check (V010). A checagem aqui existe para a
        // mensagem dizer o que falta, em vez de estourar violação de constraint.
        if (!atendimento.podeSerConcluido()) throw new AtendimentoIncompletoException();

        atendimento.concluir();

        // A consulta vira CONCLUIDA por consequência, não por escolha. É a
        // única porta: o `PATCH /status` do controller recusa CONCLUIDA
        // justamente para não existir caminho que conclua sem prontuário.
        alterarStatusConsultaService.alterarStatus(
                atendimento.getIdConsulta(), StatusConsulta.CONCLUIDA);

        return AtendimentoResponseDto.de(atendimento);
    }
}
