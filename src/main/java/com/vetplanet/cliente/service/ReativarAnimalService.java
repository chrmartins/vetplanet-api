package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.AnimalResponseDto;
import com.vetplanet.cliente.entity.AnimalEntity;
import com.vetplanet.cliente.exception.AnimalNaoEncontradoException;
import com.vetplanet.cliente.repository.AnimalRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Devolve o animal à lista. */
@Service
public class ReativarAnimalService {

    private final AnimalRepository animalRepository;

    public ReativarAnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public AnimalResponseDto reativarAnimal(UUID idAnimal) {
        AnimalEntity animal =
                animalRepository
                        .findById(idAnimal)
                        .orElseThrow(() -> new AnimalNaoEncontradoException(idAnimal));
        animal.reativar();
        return AnimalResponseDto.de(animal);
    }
}
