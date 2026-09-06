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
        /**
         * O animal ainda pode ser excluído de verdade?
         *
         * <p>Serve para a tela **não oferecer** a lixeira quando o delete
         * fosse falhar. Quem de fato impede é a chave estrangeira no banco
         * (ver {@code ExcluirAnimalService}); isto aqui é só a aparência.
         *
         * <p>TODO(historico): hoje é sempre {@code true} porque não existe
         * nada que referencie animal — {@code agendamento} e {@code prontuario}
         * não foram criados. Quando existirem, perguntar a eles. Esquecer não
         * quebra nada: a lixeira aparece, o banco recusa e a resposta é 409
         * explicando o caminho do óbito.
         */
        boolean podeExcluir,
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
                true,
                animal.getCriadoEm(),
                animal.getAtualizadoEm());
    }
}
