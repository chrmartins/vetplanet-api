package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.AnimalResponseDto;
import com.vetplanet.cliente.dto.AtualizarAnimalRequestDto;
import com.vetplanet.cliente.entity.AnimalEntity;
import com.vetplanet.cliente.exception.AnimalNaoEncontradoException;
import com.vetplanet.cliente.repository.AnimalRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Edita os dados cadastrais do animal. Situação e tutor têm caminho próprio. */
@Service
public class AtualizarAnimalService {

    private final AnimalRepository animalRepository;

    public AtualizarAnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public AnimalResponseDto atualizarAnimal(UUID idAnimal, AtualizarAnimalRequestDto request) {
        AnimalEntity animal =
                animalRepository
                        .findById(idAnimal)
                        .orElseThrow(() -> new AnimalNaoEncontradoException(idAnimal));

        animal.atualizarDados(
                request.nome(),
                request.especie(),
                request.raca(),
                request.sexo(),
                request.dataNascimento(),
                request.castrado(),
                request.observacoes());

        return AnimalResponseDto.de(animal);
    }
}
