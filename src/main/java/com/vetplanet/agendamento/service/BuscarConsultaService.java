package com.vetplanet.agendamento.service;

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
 * Uma consulta pelo id — é o que a tela de edição carrega ao abrir.
 *
 * <p>Classe própria, e não mais um método em {@code ListarConsultasService}:
 * buscar uma é outro caso de uso, e o nome daquela classe já mentiria.
 */
@Service
public class BuscarConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ResumirAnimaisService resumirAnimaisService;

    public BuscarConsultaService(
            ConsultaRepository consultaRepository, ResumirAnimaisService resumirAnimaisService) {
        this.consultaRepository = consultaRepository;
        this.resumirAnimaisService = resumirAnimaisService;
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDto buscarConsulta(UUID idConsulta) {
        ConsultaEntity consulta =
                consultaRepository
                        .findById(idConsulta)
                        .orElseThrow(() -> new ConsultaNaoEncontradaException(idConsulta));

        return ConsultaResponseDto.de(
                consulta,
                resumirAnimaisService
                        .resumirAnimais(Set.of(consulta.getIdAnimal()))
                        .get(consulta.getIdAnimal()));
    }
}
