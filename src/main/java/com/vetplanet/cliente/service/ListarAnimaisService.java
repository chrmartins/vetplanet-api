package com.vetplanet.cliente.service;

import com.vetplanet.cliente.dto.AnimalNaListaDto;
import com.vetplanet.cliente.entity.SituacaoAnimal;
import com.vetplanet.cliente.repository.AnimalRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A lista de animais, com o nome do tutor junto.
 *
 * <p><b>Uma lista só, e não "escolha o tutor, depois o bicho".</b> Ela pensa
 * "a Mel, da dona Ana" — e às vezes lembra do bicho sem lembrar de quem é.
 *
 * <p>Serve a dois lugares com a mesma consulta: o seletor do formulário de
 * agendamento, que chama sem parâmetro nenhum e recebe só quem está em
 * acompanhamento, e a tela de busca de animais, que passa o trecho do nome e
 * pode pedir os inativos.
 *
 * <p><b>Falecidos e inativos ficam de fora por padrão</b>, e isso é o que faz
 * o seletor continuar correto sem saber de nada: não se marca consulta para
 * quem não está em acompanhamento.
 */
@Service
public class ListarAnimaisService {

    private final AnimalRepository animalRepository;

    public ListarAnimaisService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * @param busca trecho do nome do animal; nulo ou vazio lista todos
     * @param incluirInativos traz também inativos, falecidos e bichos de tutor
     *     inativo — por padrão a lista mostra só quem está em acompanhamento
     */
    @Transactional(readOnly = true)
    public List<AnimalNaListaDto> listarAnimais(String busca, boolean incluirInativos) {
        // Vazio, e não nulo: parâmetro nulo dentro de `lower()` faz o Postgres
        // assumir `bytea` e a consulta estourar. Ver `buscarComTutor`.
        String trecho = busca == null ? "" : busca.trim();

        List<SituacaoAnimal> situacoes =
                incluirInativos
                        ? List.of(SituacaoAnimal.values())
                        : List.of(SituacaoAnimal.ATIVO);

        return animalRepository.buscarComTutor(trecho, situacoes, incluirInativos).stream()
                .map(AnimalNaListaDto::de)
                .toList();
    }
}
