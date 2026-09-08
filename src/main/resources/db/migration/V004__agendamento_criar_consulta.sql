-- Domínio AGENDAMENTO — os atendimentos marcados.
--
-- Depende de `cliente`: toda consulta aponta para um tutor e um animal. Por
-- isso a V003 vem antes.

create schema if not exists agendamento;

create table agendamento.consulta (
    id_consulta          uuid primary key default gen_random_uuid(),

    -- Tutor E animal, e não só o animal — parece redundante, já que dá para
    -- chegar ao tutor pelo bicho. Mas tutor muda: adoção, venda, o filho que
    -- assume o cachorro dos pais. Guardando aqui, o histórico de dois anos
    -- atrás continua dizendo quem era o responsável NA ÉPOCA, em vez de
    -- exibir o dono de hoje.
    --
    -- As FKs atravessam schema de propósito. Isso não fere a regra de "nunca
    -- acessar tabela de outro domínio": referência declarativa não é acesso, e
    -- é o que faz o banco recusar sozinho a exclusão de um animal que já tem
    -- consulta — proteção que não depende de ninguém lembrar de escrever
    -- checagem em código.
    id_tutor             uuid        not null references cliente.tutor (id_tutor),
    id_animal            uuid        not null references cliente.animal (id_animal),

    data_hora_consulta   timestamptz not null,
    duracao_minutos      integer     not null,

    -- Texto livre, e não FK para uma tabela `servico_oferecido`, que o
    -- padrao-nomenclatura.md previa. DESVIO DELIBERADO: com catálogo, a
    -- veterinária precisaria cadastrar serviços antes de conseguir marcar a
    -- primeira consulta. Vira tabela quando a repetição incomodar — e aí a
    -- migração é limpa, com coluna nova preenchida a partir dos motivos que
    -- já existirem.
    motivo_consulta      text        not null,

    status_consulta      text        not null default 'SOLICITADA',

    -- Urgência é ATRIBUTO, não status: uma consulta urgente também está
    -- solicitada ou confirmada, e as duas coisas são verdade ao mesmo tempo.
    urgente              boolean     not null default false,

    -- Nulo é permitido, ao contrário do exemplo do padrão. O endereço do
    -- tutor é opcional (o cadastro costuma começar por telefone), e exigir
    -- endereço aqui impediria de marcar a consulta antes de saber para onde
    -- ir. A tela pré-preenche com o endereço do tutor quando existe.
    endereco_atendimento text,

    observacoes          text,
    criado_em            timestamptz not null default now(),
    atualizado_em        timestamptz not null default now(),

    constraint consulta_status_valido
        check (status_consulta in ('SOLICITADA', 'CONFIRMADA', 'CANCELADA', 'CONCLUIDA')),
    constraint consulta_duracao_positiva
        check (duracao_minutos > 0 and duracao_minutos <= 600),
    constraint consulta_motivo_nao_vazio
        check (length(trim(motivo_consulta)) > 0)
);

-- A Agenda é sempre lida por intervalo de tempo: o mês na grade, o dia no
-- painel lateral. Este índice é o que sustenta as duas.
create index consulta_por_data on agendamento.consulta (data_hora_consulta);

-- "Todas as consultas da Mel" — o histórico do animal na ficha.
create index consulta_por_animal on agendamento.consulta (id_animal, data_hora_consulta desc);

comment on table  agendamento.consulta is 'Atendimento marcado. Nunca excluída: muda de status';
comment on column agendamento.consulta.id_tutor is 'Responsável na época do atendimento; não muda se o animal trocar de tutor';
comment on column agendamento.consulta.urgente is 'Atributo, não status — convive com solicitada/confirmada';
comment on column agendamento.consulta.endereco_atendimento is 'Onde atender. Nulo enquanto não se sabe';
