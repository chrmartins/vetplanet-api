package com.vetplanet.agendamento.service;

import com.vetplanet.agendamento.dto.ConsultaResponseDto;
import com.vetplanet.agendamento.entity.ConsultaEntity;
import com.vetplanet.agendamento.repository.ConsultaRepository;
import com.vetplanet.cliente.dto.ResumoAnimalDto;
import com.vetplanet.cliente.service.ResumirAnimaisService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * As consultas de um intervalo — é como a Agenda lê: o mês na grade, o dia no
 * painel lateral.
 *
 * <p>Os nomes de animal e tutor são resolvidos **numa chamada só** para todo o
 * intervalo. Resolver por consulta seria N+1, e um mês cheio tem dezenas.
 */
@Service
public class ListarConsultasService {

    private final ConsultaRepository consultaRepository;
    private final ResumirAnimaisService resumirAnimaisService;

    public ListarConsultasService(
            ConsultaRepository consultaRepository, ResumirAnimaisService resumirAnimaisService) {
        this.consultaRepository = consultaRepository;
        this.resumirAnimaisService = resumirAnimaisService;
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDto> listarConsultas(OffsetDateTime de, OffsetDateTime ate) {
        List<ConsultaEntity> consultas =
                consultaRepository.findByDataHoraBetweenOrderByDataHoraAsc(de, ate);
        return montar(consultas);
    }

    /** O histórico de um animal, para a ficha dele. */
    @Transactional(readOnly = true)
    public List<ConsultaResponseDto> listarConsultasDoAnimal(UUID idAnimal) {
        return montar(consultaRepository.findByIdAnimalOrderByDataHoraDesc(idAnimal));
    }

    private List<ConsultaResponseDto> montar(List<ConsultaEntity> consultas) {
        if (consultas.isEmpty()) return List.of();

        Map<UUID, ResumoAnimalDto> animais =
                resumirAnimaisService.resumirAnimais(
                        consultas.stream().map(ConsultaEntity::getIdAnimal).collect(Collectors.toSet()));

        return consultas.stream()
                .map(consulta -> ConsultaResponseDto.de(consulta, animais.get(consulta.getIdAnimal())))
                .toList();
    }
}
