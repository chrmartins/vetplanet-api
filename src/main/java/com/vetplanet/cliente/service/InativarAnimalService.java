package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.AnimalResponseDto;
import com.vetplanet.cliente.entity.AnimalEntity;
import com.vetplanet.cliente.exception.AnimalNaoEncontradoException;
import com.vetplanet.cliente.repository.AnimalRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tira o animal da lista sem apagar nada.
 *
 * <p>É o substituto da exclusão para quem <b>já tem histórico</b>: consulta ou
 * prontuário travam o delete, e sem esta saída o cadastro errado ficaria
 * visível para sempre.
 *
 * <p>Não significa "parou de aparecer" — ver {@code SituacaoAnimal}.
 */
@Service
public class InativarAnimalService {

    private final AnimalRepository animalRepository;

    public InativarAnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public AnimalResponseDto inativarAnimal(UUID idAnimal) {
        AnimalEntity animal =
                animalRepository
                        .findById(idAnimal)
                        .orElseThrow(() -> new AnimalNaoEncontradoException(idAnimal));
        animal.inativar();
        return AnimalResponseDto.de(animal);
    }
}
