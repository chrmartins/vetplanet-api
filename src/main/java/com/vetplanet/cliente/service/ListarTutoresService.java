package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.TutorResponseDto;
import com.vetplanet.cliente.entity.TutorEntity;
import com.vetplanet.cliente.repository.AnimalRepository;
import com.vetplanet.cliente.repository.TutorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lista tutores, com busca opcional por nome. */
@Service
public class ListarTutoresService {

    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;

    public ListarTutoresService(
            TutorRepository tutorRepository, AnimalRepository animalRepository) {
        this.tutorRepository = tutorRepository;
        this.animalRepository = animalRepository;
    }

    /**
     * @param busca trecho do nome; nulo ou em branco lista todos
     * @param incluirInativos falso devolve só quem está ativo
     */
    @Transactional(readOnly = true)
    public List<TutorResponseDto> listarTutores(String busca, boolean incluirInativos) {
        List<TutorEntity> tutores = buscar(busca, incluirInativos);

        // TODO(escala): uma contagem por tutor é N+1. Aceitável para a
        // clientela de uma veterinária; vira uma consulta agrupada quando a
        // listagem passar a doer (ou quando entrar paginação).
        return tutores.stream()
                .map(tutor -> TutorResponseDto.resumo(tutor, animalRepository.countByTutorId(tutor.getId())))
                .toList();
    }

    private List<TutorEntity> buscar(String busca, boolean incluirInativos) {
        if (busca != null && !busca.isBlank()) {
            List<TutorEntity> encontrados =
                    tutorRepository.findByNomeCompletoContainingIgnoreCaseOrderByNomeCompletoAsc(
                            busca.trim());
            return incluirInativos ? encontrados : encontrados.stream().filter(TutorEntity::isAtivo).toList();
        }
        return incluirInativos
                ? tutorRepository.findAllByOrderByNomeCompletoAsc()
                : tutorRepository.findByAtivoTrueOrderByNomeCompletoAsc();
    }
}
