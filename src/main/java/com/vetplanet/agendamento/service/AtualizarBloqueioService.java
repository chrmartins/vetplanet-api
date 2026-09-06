package com.vetplanet.agendamento.service;

import com.vetplanet.agendamento.dto.BloqueioResponseDto;
import com.vetplanet.agendamento.dto.CriarBloqueioRequestDto;
import com.vetplanet.agendamento.entity.BloqueioEntity;
import com.vetplanet.agendamento.exception.BloqueioNaoEncontradoException;
import com.vetplanet.agendamento.repository.BloqueioRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Substitui o conteúdo de um bloqueio.
 *
 * <p><b>Substitui, não remenda</b> — inclusive a forma: um bloqueio semanal
 * pode virar período. Por isso o corpo é o mesmo de criar e passa pelas mesmas
 * regras ({@link FormaDoBloqueio}); um PATCH com campos soltos deixaria
 * conviver dia da semana e datas, que é a linha que o banco recusa.
 *
 * <p>Existe para que mudar o almoço das 12h para as 13h não seja apagar e
 * recriar. Não há histórico preso a um bloqueio, então a edição é direta —
 * nada de registro de retificação como no prontuário.
 */
@Service
public class AtualizarBloqueioService {

    private final BloqueioRepository bloqueioRepository;

    public AtualizarBloqueioService(BloqueioRepository bloqueioRepository) {
        this.bloqueioRepository = bloqueioRepository;
    }

    @Transactional
    public BloqueioResponseDto atualizarBloqueio(
            UUID idBloqueio, CriarBloqueioRequestDto request) {
        BloqueioEntity bloqueio =
                bloqueioRepository
                        .findById(idBloqueio)
                        .orElseThrow(() -> new BloqueioNaoEncontradoException(idBloqueio));

        FormaDoBloqueio.de(request).aplicarEm(bloqueio);

        return BloqueioResponseDto.de(bloqueio);
    }
}
