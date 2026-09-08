package com.vetplanet.agendamento.repository;

import com.vetplanet.agendamento.entity.ConsultaEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso a {@code agendamento.consulta}. */
public interface ConsultaRepository extends JpaRepository<ConsultaEntity, UUID> {

    /**
     * As consultas de um intervalo — é como a Agenda sempre lê: o mês na
     * grade, o dia no painel lateral. O índice `consulta_por_data` sustenta.
     */
    List<ConsultaEntity> findByDataHoraBetweenOrderByDataHoraAsc(
            OffsetDateTime inicio, OffsetDateTime fim);

    /** O histórico de um animal, do mais recente para o mais antigo. */
    List<ConsultaEntity> findByIdAnimalOrderByDataHoraDesc(UUID idAnimal);

    /** Usado para saber se um animal pode ser excluído. */
    boolean existsByIdAnimal(UUID idAnimal);
}
