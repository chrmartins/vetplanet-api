package com.vetplanet.prontuario.repository;

import com.vetplanet.prontuario.entity.AlteracaoAtendimentoEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso a {@code prontuario.alteracao_atendimento}. */
public interface AlteracaoAtendimentoRepository
        extends JpaRepository<AlteracaoAtendimentoEntity, UUID> {

    /** O rastro de um atendimento, do mais recente para o mais antigo. */
    List<AlteracaoAtendimentoEntity> findByIdAtendimentoOrderByCriadoEmDesc(UUID idAtendimento);
}
