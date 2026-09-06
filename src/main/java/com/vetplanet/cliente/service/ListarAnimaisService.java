package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.ResumoAnimalDto;
import com.vetplanet.cliente.entity.SituacaoAnimal;
import com.vetplanet.cliente.repository.AnimalRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Todos os animais em acompanhamento, com o nome do tutor junto.
 *
 * <p>Existe para o seletor do formulário de agendamento: ela pensa "a Mel, da
 * dona Ana", não "o tutor Ana, e dentro dele a Mel". Uma lista só, com os dois
 * nomes, evita o passo intermediário de escolher tutor antes do bicho.
 *
 * <p>Devolve o mesmo {@code ResumoAnimalDto} que atravessa a fronteira para
 * `agendamento` — é a mesma informação, e duplicar o record só para mudar o
 * nome não ganharia nada.
 *
 * <p>Falecidos e inativos ficam de fora: não se marca consulta para eles.
 */
@Service
public class ListarAnimaisService {

    private final AnimalRepository animalRepository;

    public ListarAnimaisService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional(readOnly = true)
    public List<ResumoAnimalDto> listarAnimaisAtivos() {
        return animalRepository.buscarAtivosComTutor(SituacaoAnimal.ATIVO).stream()
                .map(
                        animal ->
                                new ResumoAnimalDto(
                                        animal.getId(),
                                        animal.getNome(),
                                        animal.getTutor().getId(),
                                        animal.getTutor().getNomeCompleto()))
                .toList();
    }
}
