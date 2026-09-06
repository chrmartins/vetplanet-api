-- Bloqueio semanal passa a valer para VÁRIOS dias da semana.
--
-- A V005 guardava um dia por linha, e "almoço de segunda a sexta" virava cinco
-- bloqueios. Isso não é detalhe de tela: ela criou UMA coisa, e teria de
-- apagar cinco para desfazer, ou editar cinco para mudar o horário. Uma linha
-- por bloqueio faz o registro corresponder ao que ela fez.
--
-- Migração nova em vez de correção da V005, seguindo a regra do projeto —
-- a V005 já foi aplicada em banco de desenvolvimento, e reescrevê-la quebraria
-- o checksum do Flyway de quem já a tem.

alter table agendamento.bloqueio
    add column dias_da_semana smallint[];

-- Cada linha antiga vira um array de um elemento. Nada se perde.
update agendamento.bloqueio
   set dias_da_semana = array[dia_da_semana]
 where dia_da_semana is not null;

-- Os dois checks citam a coluna antiga; o `drop column` os levaria junto, mas
-- derrubá-los aqui deixa explícito o que está saindo.
alter table agendamento.bloqueio
    drop constraint bloqueio_forma_unica,
    drop constraint bloqueio_dia_da_semana_valido,
    drop column dia_da_semana;

alter table agendamento.bloqueio
    -- Mesma regra da V005, agora sobre o array: ou repete toda semana, ou vale
    -- para um período. Nunca as duas.
    add constraint bloqueio_forma_unica
        check (
            (dias_da_semana is not null and data_inicio is null and data_fim is null)
            or
            (dias_da_semana is null and data_inicio is not null and data_fim is not null)
        ),

    -- `<@` é continência: todo valor do array tem de estar em 0..6. Junto com o
    -- comprimento mínimo, impede tanto o dia 9 quanto o array vazio — que
    -- passaria pelo check de forma e criaria um bloqueio semanal que nunca cai
    -- em dia nenhum.
    add constraint bloqueio_dias_da_semana_validos
        check (
            dias_da_semana is null
            or (
                array_length(dias_da_semana, 1) between 1 and 7
                and dias_da_semana <@ array[0, 1, 2, 3, 4, 5, 6]::smallint[]
            )
        );

comment on column agendamento.bloqueio.dias_da_semana is '0=domingo..6=sábado (convenção do Postgres e do JS, não a do DayOfWeek do Java). Vários dias por bloqueio';
