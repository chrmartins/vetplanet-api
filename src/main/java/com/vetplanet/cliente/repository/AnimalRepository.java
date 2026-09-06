package com.vetplanet.cliente.repository;

import com.vetplanet.cliente.entity.AnimalEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** Acesso a {@code cliente.animal}. */
public interface AnimalRepository extends JpaRepository<AnimalEntity, UUID> {

    List<AnimalEntity> findByTutorIdOrderByNomeAsc(UUID idTutor);

    long countByTutorId(UUID idTutor);

    /**
     * Animal com o tutor já carregado.
     *
     * <p>A relação é LAZY, então quem precisa do tutor junto pede aqui. Sem o
     * {@code join fetch}, montar o DTO fora da transação estoura com
     * {@code LazyInitializationException} — e dentro dela vira consulta extra
     * a cada animal.
     */
    @Query("select a from AnimalEntity a join fetch a.tutor where a.id = :idAnimal")
    Optional<AnimalEntity> buscarComTutor(UUID idAnimal);

    /**
     * Vários animais com o tutor junto, numa consulta só.
     *
     * <p>Usado por {@code ResumirAnimaisService} para a Agenda montar
     * "Thor · J. Lima" sem disparar uma consulta por linha.
     */
    @Query("select a from AnimalEntity a join fetch a.tutor where a.id in :ids")
    List<AnimalEntity> buscarComTutorPorIds(java.util.Collection<UUID> ids);

    /** Busca por parte do nome do animal — "a gata Mel", sem saber o tutor. */
    @Query(
            "select a from AnimalEntity a join fetch a.tutor "
                    + "where lower(a.nome) like lower(concat('%', :trecho, '%')) "
                    + "order by a.nome asc")
    List<AnimalEntity> buscarPorNomeComTutor(String trecho);
}
