-- Domínio AGENDAMENTO — o tempo que NÃO é para atender.
--
-- Almoço, congresso, consulta médica dela, feriado. Sem isso, a Agenda dá a
-- entender que todo horário vazio está livre, e o aviso de conflito da tela de
-- marcação só enxerga consultas: marcar em cima do almoço não avisa nada.

create table agendamento.bloqueio (
    id_bloqueio     uuid primary key default gen_random_uuid(),

    -- "Almoço", "Congresso de anestesia", "Médico". Texto livre pelo mesmo
    -- motivo de `motivo_consulta`: catálogo obrigaria a cadastrar tipos antes
    -- de conseguir bloquear a primeira tarde.
    motivo          text     not null,

    -- ── Duas formas de bloqueio, uma tabela ───────────────────────────────
    --
    -- SEMANAL: repete toda semana no mesmo dia (`dia_da_semana` preenchido).
    --   É o almoço, e o "não atendo domingo".
    -- PERÍODO: um trecho de calendário com começo e fim (`data_inicio` e
    --   `data_fim`). É o congresso, a viagem, o feriado.
    --
    -- Uma tabela e não duas porque as duas formas compartilham tudo o mais e
    -- os dois consumidores — desenhar o dia e avisar de conflito — sempre
    -- precisam das duas juntas. O check abaixo é o que impede a linha híbrida
    -- que não seria nem uma coisa nem outra.
    --
    -- 0 = domingo … 6 = sábado. Mesma convenção do `extract(dow ...)` do
    -- Postgres e do `getDay()` do JavaScript, que é quem desenha a grade.
    -- NÃO é a do `DayOfWeek` do Java (1 = segunda), de propósito: um único
    -- lugar convertendo erra menos que três lugares convertendo.
    dia_da_semana   smallint,
    data_inicio     date,
    data_fim        date,

    -- ── Horário, em tempo CIVIL da clínica ────────────────────────────────
    --
    -- DESVIO DELIBERADO da regra "persistir em UTC". Aquela regra vale para
    -- INSTANTE: uma consulta acontece num ponto do tempo, e guardá-la em UTC é
    -- o que a mantém correta em qualquer fuso. Um bloqueio semanal não é um
    -- instante — "almoço ao meio-dia" é meio-dia em São Paulo hoje, amanhã e
    -- depois do horário de verão voltar. Convertido para UTC ele viraria
    -- 15:00Z e passaria a almoçar às 11h no dia em que o offset mudasse.
    --
    -- Nulo nos dois = o dia inteiro. É o congresso, que não tem hora.
    hora_inicio     time,
    hora_fim        time,

    criado_em       timestamptz not null default now(),
    atualizado_em   timestamptz not null default now(),

    constraint bloqueio_motivo_nao_vazio
        check (length(trim(motivo)) > 0),

    -- Exatamente uma das duas formas. Sem isto, uma linha com dia da semana E
    -- datas seria aceita pelo banco e ninguém saberia qual regra vale.
    constraint bloqueio_forma_unica
        check (
            (dia_da_semana is not null and data_inicio is null and data_fim is null)
            or
            (dia_da_semana is null and data_inicio is not null and data_fim is not null)
        ),

    constraint bloqueio_dia_da_semana_valido
        check (dia_da_semana between 0 and 6),

    constraint bloqueio_periodo_ordenado
        check (data_fim >= data_inicio),

    -- Ou tem hora (as duas) ou é dia inteiro (nenhuma). Só uma preenchida
    -- seria um bloqueio sem começo ou sem fim.
    constraint bloqueio_horario_completo
        check ((hora_inicio is null) = (hora_fim is null)),

    constraint bloqueio_horario_ordenado
        check (hora_fim > hora_inicio)
);

-- A Agenda pede os bloqueios do período que está exibindo. São poucas linhas
-- (uma clínica tem punhado de bloqueios, não milhares), então o índice é pela
-- data e os semanais saem juntos por não terem data nenhuma.
create index bloqueio_por_periodo on agendamento.bloqueio (data_inicio, data_fim);

comment on table  agendamento.bloqueio is 'Tempo indisponível: almoço, congresso, feriado. Duas formas — semanal ou período';
comment on column agendamento.bloqueio.dia_da_semana is '0=domingo..6=sábado (convenção do Postgres e do JS, não a do DayOfWeek do Java)';
comment on column agendamento.bloqueio.hora_inicio is 'Hora CIVIL de America/Sao_Paulo, não UTC — bloqueio semanal não é instante. Nulo = dia inteiro';
