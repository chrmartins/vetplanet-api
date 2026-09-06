package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.AnimalResponseDto;
import com.vetplanet.cliente.exception.AnimalNaoEncontradoException;
import com.vetplanet.cliente.repository.AnimalRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Um animal, com o tutor carregado junto. */
@Service
public class BuscarAnimalService {

    private final AnimalRepository animalRepository;

    public BuscarAnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional(readOnly = true)
    public AnimalResponseDto buscarAnimal(UUID idAnimal) {
        return AnimalResponseDto.de(
                animalRepository
                        .buscarComTutor(idAnimal)
                        .orElseThrow(() -> new AnimalNaoEncontradoException(idAnimal)));
    }
}
