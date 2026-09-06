package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.AnimalResponseDto;
import com.vetplanet.cliente.dto.CriarAnimalRequestDto;
import com.vetplanet.cliente.entity.AnimalEntity;
import com.vetplanet.cliente.entity.TutorEntity;
import com.vetplanet.cliente.exception.TutorNaoEncontradoException;
import com.vetplanet.cliente.repository.AnimalRepository;
import com.vetplanet.cliente.repository.TutorRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Adiciona mais um animal a um tutor que já existe — o segundo gato. */
@Service
public class AdicionarAnimalService {

    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;

    public AdicionarAnimalService(
            TutorRepository tutorRepository, AnimalRepository animalRepository) {
        this.tutorRepository = tutorRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional
    public AnimalResponseDto adicionarAnimal(UUID idTutor, CriarAnimalRequestDto request) {
        TutorEntity tutor =
                tutorRepository
                        .findById(idTutor)
                        .orElseThrow(() -> new TutorNaoEncontradoException(idTutor));

        AnimalEntity animal =
                AnimalEntity.criar(
                        tutor,
                        request.nome(),
                        request.especie(),
                        request.raca(),
                        request.sexo(),
                        request.dataNascimento(),
                        request.castrado(),
                        request.observacoes());

        animalRepository.save(animal);
        return AnimalResponseDto.de(animal);
    }
}
