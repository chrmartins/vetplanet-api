package com.vetplanet.cliente.dto;

import com.vetplanet.cliente.entity.AnimalEntity;
import com.vetplanet.cliente.entity.EspecieAnimal;
import com.vetplanet.cliente.entity.SexoAnimal;
import com.vetplanet.cliente.entity.SituacaoAnimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Animal como sai da API. */
public record AnimalResponseDto(
        UUID idAnimal,
        UUID idTutor,
        String nome,
        EspecieAnimal especie,
        String raca,
        SexoAnimal sexo,
        LocalDate dataNascimento,
        Boolean castrado,
        String observacoes,
        SituacaoAnimal situacao,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm) {

    /**
     * Só use com o tutor carregado (ver {@code AnimalRepository#buscarComTutor})
     * ou dentro da transação — {@code getTutor().getId()} toca a relação LAZY.
     */
    public static AnimalResponseDto de(AnimalEntity animal) {
        return new AnimalResponseDto(
                animal.getId(),
                animal.getTutor().getId(),
                animal.getNome(),
                animal.getEspecie(),
                animal.getRaca(),
                animal.getSexo(),
                animal.getDataNascimento(),
                animal.getCastrado(),
                animal.getObservacoes(),
                animal.getSituacao(),
                animal.getCriadoEm(),
                animal.getAtualizadoEm());
    }
}
