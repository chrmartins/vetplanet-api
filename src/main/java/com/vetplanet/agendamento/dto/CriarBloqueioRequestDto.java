package com.vetplanet.agendamento.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dados para bloquear um tempo na agenda.
 *
 * <p><b>Um DTO para as duas formas</b>, e a validação cruzada fica no service.
 * Dois DTOs (e dois endpoints) obrigariam a tela a escolher a rota antes de a
 * pessoa terminar de preencher, e o que ela preenche é um formulário só com
 * uma pergunta no meio: "repete toda semana?".
 *
 * <p>Preencha {@code diaDaSemana} <b>ou</b> o par {@code dataInicio}/{@code
 * dataFim} — nunca os dois. {@code horaInicio} e {@code horaFim} vazios
 * bloqueiam o dia inteiro.
 *
 * <p>As horas são <b>civis</b>, do fuso da clínica, e não instantes UTC. A
 * razão está na migração {@code V005}: bloqueio semanal não é um ponto do
 * tempo, é uma hora do relógio da parede.
 */
public record CriarBloqueioRequestDto(
        @NotBlank(message = "Informe o motivo do bloqueio.")
                @Size(max = 200, message = "Motivo muito longo.")
                String motivo,
        @Min(value = 0, message = "Dia da semana inválido.")
                @Max(value = 6, message = "Dia da semana inválido.")
                Short diaDaSemana,
        LocalDate dataInicio,
        LocalDate dataFim,
        LocalTime horaInicio,
        LocalTime horaFim) {}
