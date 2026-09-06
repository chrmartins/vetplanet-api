package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.AnimalResponseDto;
import com.vetplanet.cliente.entity.AnimalEntity;
import com.vetplanet.cliente.exception.AnimalNaoEncontradoException;
import com.vetplanet.cliente.repository.AnimalRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registra o óbito de um animal.
 *
 * <p>Caso de uso próprio, e não um campo de status a ser editado: é um fato
 * clínico, não um ajuste cadastral. Como campo de formulário, aconteceria por
 * descuido num envio distraído.
 *
 * <p>O registro <b>não apaga nada</b> — consultas e prontuários continuam
 * inteiros, que é o que a veterinária vai querer consultar depois. É essa a
 * diferença para {@link ExcluirAnimalService}, e é por isso que a mensagem de
 * exclusão barrada aponta para cá.
 */
@Service
public class RegistrarObitoService {

    private static final Logger log = LoggerFactory.getLogger(RegistrarObitoService.class);

    private final AnimalRepository animalRepository;

    public RegistrarObitoService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public AnimalResponseDto registrarObito(UUID idAnimal) {
        AnimalEntity animal =
                animalRepository
                        .findById(idAnimal)
                        .orElseThrow(() -> new AnimalNaoEncontradoException(idAnimal));

        animal.registrarObito();
        log.info("Óbito registrado para o animal {}", idAnimal);
        return AnimalResponseDto.de(animal);
    }
}
