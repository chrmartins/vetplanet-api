package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.AtualizarTutorRequestDto;
import com.vetplanet.cliente.dto.TutorResponseDto;
import com.vetplanet.cliente.entity.TutorEntity;
import com.vetplanet.cliente.exception.TutorNaoEncontradoException;
import com.vetplanet.cliente.repository.AnimalRepository;
import com.vetplanet.cliente.repository.TutorRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Edita os dados cadastrais do tutor. Os animais têm caminho próprio. */
@Service
public class AtualizarTutorService {

    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;

    public AtualizarTutorService(
            TutorRepository tutorRepository, AnimalRepository animalRepository) {
        this.tutorRepository = tutorRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional
    public TutorResponseDto atualizarTutor(UUID idTutor, AtualizarTutorRequestDto request) {
        TutorEntity tutor =
                tutorRepository
                        .findById(idTutor)
                        .orElseThrow(() -> new TutorNaoEncontradoException(idTutor));

        tutor.atualizarDados(
                request.nomeCompleto(),
                request.telefoneWhatsapp(),
                request.email(),
                request.endereco() == null ? null : request.endereco().paraEntidade(),
                request.observacoes());

        return TutorResponseDto.completo(tutor, animalRepository.findByTutorIdOrderByNomeAsc(idTutor));
    }
}
