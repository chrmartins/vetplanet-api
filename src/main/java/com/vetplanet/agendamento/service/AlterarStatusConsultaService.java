package com.vetplanet.agendamento.service;

import com.vetplanet.agendamento.dto.ConsultaResponseDto;
import com.vetplanet.agendamento.entity.ConsultaEntity;
import com.vetplanet.agendamento.entity.StatusConsulta;
import com.vetplanet.agendamento.exception.ConsultaNaoEncontradaException;
import com.vetplanet.agendamento.repository.ConsultaRepository;
import com.vetplanet.cliente.service.ResumirAnimaisService;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Confirma, cancela ou conclui uma consulta.
 *
 * <p><b>Cancelar é status, não exclusão.</b> A consulta cancelada continua no
 * histórico — é informação clínica e financeira, e some da agenda pela tela,
 * não do banco.
 */
@Service
public class AlterarStatusConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ResumirAnimaisService resumirAnimaisService;

    public AlterarStatusConsultaService(
            ConsultaRepository consultaRepository, ResumirAnimaisService resumirAnimaisService) {
        this.consultaRepository = consultaRepository;
        this.resumirAnimaisService = resumirAnimaisService;
    }

    @Transactional
    public ConsultaResponseDto alterarStatus(UUID idConsulta, StatusConsulta novoStatus) {
        ConsultaEntity consulta =
                consultaRepository
                        .findById(idConsulta)
                        .orElseThrow(() -> new ConsultaNaoEncontradaException(idConsulta));

        consulta.alterarStatus(novoStatus);

        return ConsultaResponseDto.de(
                consulta,
                resumirAnimaisService.resumirAnimais(Set.of(consulta.getIdAnimal()))
                        .get(consulta.getIdAnimal()));
    }
}
