package com.vetplanet.agendamento.service;

import com.vetplanet.agendamento.repository.ConsultaRepository;
import com.vetplanet.cliente.service.HistoricoDoAnimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responde a `cliente` se um animal já tem consulta.
 *
 * <p>É a implementação da porta {@code HistoricoDoAnimal}, declarada em
 * `cliente`. A dependência aponta para lá, nunca o contrário — sem isso os
 * dois módulos se importariam mutuamente.
 */
@Service
public class HistoricoDeConsultas implements HistoricoDoAnimal {

    private final ConsultaRepository consultaRepository;

    public HistoricoDeConsultas(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean temHistorico(UUID idAnimal) {
        return consultaRepository.existsByIdAnimal(idAnimal);
    }
}
