package com.vetplanet.agendamento.service;

import com.vetplanet.agendamento.dto.AtualizarConsultaRequestDto;
import com.vetplanet.agendamento.dto.ConsultaResponseDto;
import com.vetplanet.agendamento.entity.ConsultaEntity;
import com.vetplanet.agendamento.exception.ConsultaNaoEncontradaException;
import com.vetplanet.agendamento.repository.ConsultaRepository;
import com.vetplanet.cliente.service.ResumirAnimaisService;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Corrige ou remarca uma consulta.
 *
 * <p><b>Existe para que remarcar não seja cancelar e criar de novo.</b> Aquele
 * caminho escreve um fato falso no histórico: {@code CANCELADA} quer dizer que
 * o atendimento não aconteceu, e um atendimento remarcado aconteceu — só que
 * mais tarde. De quebra, perdia as observações já digitadas.
 *
 * <p><b>Não trava por status</b>, nem mesmo em consulta concluída. A trava, no
 * dia em que existir, tem de vir do que está preso à consulta — o prontuário —
 * e não da passagem do tempo. É a mesma lógica que já vale para excluir animal:
 * quem recusa é a chave estrangeira do histórico, não uma checagem de estado.
 * Enquanto não há prontuário, não há o que proteger, e travar agora só
 * impediria corrigir um erro de digitação.
 *
 * <p>Chama os dois métodos da entidade porque são coisas diferentes:
 * {@code reagendar} move o atendimento no tempo, {@code atualizarDados} conserta
 * o que está escrito sobre ele.
 */
@Service
public class AtualizarConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ResumirAnimaisService resumirAnimaisService;

    public AtualizarConsultaService(
            ConsultaRepository consultaRepository, ResumirAnimaisService resumirAnimaisService) {
        this.consultaRepository = consultaRepository;
        this.resumirAnimaisService = resumirAnimaisService;
    }

    @Transactional
    public ConsultaResponseDto atualizarConsulta(
            UUID idConsulta, AtualizarConsultaRequestDto request) {
        ConsultaEntity consulta =
                consultaRepository
                        .findById(idConsulta)
                        .orElseThrow(() -> new ConsultaNaoEncontradaException(idConsulta));

        consulta.reagendar(request.dataHora(), request.duracaoMinutos());
        consulta.atualizarDados(
                request.motivo(),
                Boolean.TRUE.equals(request.urgente()),
                request.enderecoAtendimento(),
                request.observacoes());

        return ConsultaResponseDto.de(
                consulta,
                resumirAnimaisService
                        .resumirAnimais(Set.of(consulta.getIdAnimal()))
                        .get(consulta.getIdAnimal()));
    }
}
