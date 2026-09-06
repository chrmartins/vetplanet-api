package com.vetplanet.agendamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Tempo que não é para atender: almoço, congresso, feriado, médico.
 *
 * <p><b>Duas formas na mesma tabela.</b> Semanal ({@code diaDaSemana}
 * preenchido) é o que repete — o almoço. Período ({@code dataInicio} e
 * {@code dataFim}) é o trecho de calendário — o congresso. O banco recusa a
 * linha que tentar ser as duas; ver {@code V005}.
 *
 * <p><b>Horário em tempo civil, não em UTC.</b> É desvio consciente da regra
 * do projeto, e a razão está na migração: "almoço ao meio-dia" não é um
 * instante, é uma hora do relógio da parede. Guardado em UTC, ele almoçaria
 * uma hora mais cedo no dia em que o offset do fuso mudasse.
 *
 * <p>Por isso {@code LocalTime} e {@code LocalDate}, e não
 * {@code OffsetDateTime}: o tipo carrega a intenção. Quem for comparar um
 * bloqueio com uma consulta precisa antes converter a consulta para o dia e a
 * hora de São Paulo — nunca o contrário.
 *
 * <p><b>Não guarda quem bloqueou.</b> O sistema é em primeira pessoa: há uma
 * agenda só, a do assinante.
 */
@Entity
@Table(name = "bloqueio", schema = "agendamento")
public class BloqueioEntity {

    @Id
    @GeneratedValue
    @Column(name = "id_bloqueio", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    /** 0 = domingo … 6 = sábado. Nulo quando o bloqueio é um período. */
    @Column(name = "dia_da_semana")
    private Short diaDaSemana;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    /** Inclusivo: um congresso de 20 a 22 bloqueia o dia 22 inteiro também. */
    @Column(name = "data_fim")
    private LocalDate dataFim;

    /** Hora civil de America/Sao_Paulo. Nulo (com {@code horaFim}) = dia inteiro. */
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    /** Exigido pelo JPA — não usar no código da aplicação. */
    protected BloqueioEntity() {}

    private BloqueioEntity(
            String motivo,
            Short diaDaSemana,
            LocalDate dataInicio,
            LocalDate dataFim,
            LocalTime horaInicio,
            LocalTime horaFim) {
        this.motivo = motivo.trim();
        this.diaDaSemana = diaDaSemana;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.criadoEm = OffsetDateTime.now(ZoneOffset.UTC);
        this.atualizadoEm = this.criadoEm;
    }

    /** Bloqueio que repete toda semana — o almoço, o dia que ela não atende. */
    public static BloqueioEntity semanal(
            String motivo, short diaDaSemana, LocalTime horaInicio, LocalTime horaFim) {
        return new BloqueioEntity(motivo, diaDaSemana, null, null, horaInicio, horaFim);
    }

    /**
     * Bloqueio de um trecho do calendário — o congresso, a viagem.
     *
     * <p>{@code horaInicio} e {@code horaFim} nulos bloqueiam os dias
     * inteiros; preenchidos, valem para <b>todos</b> os dias do período (uma
     * semana de plantão das 18h às 22h, por exemplo).
     */
    public static BloqueioEntity periodo(
            String motivo,
            LocalDate dataInicio,
            LocalDate dataFim,
            LocalTime horaInicio,
            LocalTime horaFim) {
        return new BloqueioEntity(motivo, null, dataInicio, dataFim, horaInicio, horaFim);
    }

    public UUID getId() {
        return id;
    }

    public String getMotivo() {
        return motivo;
    }

    public Short getDiaDaSemana() {
        return diaDaSemana;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
