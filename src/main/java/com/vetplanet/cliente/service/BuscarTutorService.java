package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.TutorResponseDto;
import com.vetplanet.cliente.entity.TutorEntity;
import com.vetplanet.cliente.exception.TutorNaoEncontradoException;
import com.vetplanet.cliente.repository.AnimalRepository;
import com.vetplanet.cliente.repository.TutorRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** A ficha do tutor, com os animais dele. */
@Service
public class BuscarTutorService {

    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;

    public BuscarTutorService(TutorRepository tutorRepository, AnimalRepository animalRepository) {
        this.tutorRepository = tutorRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional(readOnly = true)
    public TutorResponseDto buscarTutor(UUID idTutor) {
        TutorEntity tutor =
                tutorRepository
                        .findById(idTutor)
                        .orElseThrow(() -> new TutorNaoEncontradoException(idTutor));

        return TutorResponseDto.completo(tutor, animalRepository.findByTutorIdOrderByNomeAsc(idTutor));
    }
}
