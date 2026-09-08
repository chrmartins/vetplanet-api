-- Domínio PRONTUÁRIO — o registro clínico.
--
-- Não é modelo livre: o prontuário médico-veterinário é documento
-- técnico-legal com conteúdo definido pela Resolução CFMV nº 1.321/2020,
-- alterada pela nº 1.653/2025. As colunas abaixo citam o inciso do Art. 9º
-- que cada uma atende — quem for mexer aqui precisa saber que está mexendo
-- numa exigência, não numa preferência.
--
-- Ver `docs/prontuario.md` para o levantamento completo, inclusive o que a
-- norma NÃO exige (não há exigência de assinatura ICP-Brasil, e a guarda
-- mínima é de 5 anos, não 20 — as duas coisas circulam erradas por aí).

create schema if not exists prontuario;

create table prontuario.atendimento (
    id_atendimento uuid primary key default gen_random_uuid(),

    -- Um atendimento por consulta, e a consulta é a âncora: dela vêm data,
    -- hora e o animal. `unique` porque registrar duas vezes o mesmo
    -- atendimento é engano, não caso de uso.
    --
    -- A FK atravessa schema, como em `agendamento.consulta`: referência
    -- declarativa não é acesso, e é ela que impede apagar uma consulta que já
    -- virou prontuário.
    id_consulta    uuid not null unique references agendamento.consulta (id_consulta),

    -- Redundante com a consulta, e de propósito: é o que sustenta "o histórico
    -- da Mel" sem `prontuario` precisar atravessar `agendamento` para saber de
    -- quem é o registro.
    id_animal      uuid not null references cliente.animal (id_animal),

    -- ── Art. 9º, II e VIII — quem assina ─────────────────────────────────
    --
    -- A norma exige NOME COMPLETO E NÚMERO DE CRMV. Guardados como CÓPIA, não
    -- só a FK: se o nome ou o número mudarem no cadastro, o documento antigo
    -- continua dizendo o que valia na época. Mesma razão do `id_tutor` na
    -- consulta.
    id_veterinario   uuid not null references acesso.usuario (id_usuario),
    nome_veterinario text not null,
    crmv_veterinario text not null,

    -- ── Art. 9º, I — data, hora e LOCAL ──────────────────────────────────
    --
    -- Data e hora vêm da consulta. O local é obrigatório aqui, embora seja
    -- opcional na consulta: marcar sem saber o endereço é normal, atender sem
    -- saber onde não é.
    local_atendimento text not null,

    -- ── Art. 9º, III a VII — o registro clínico ──────────────────────────
    anamnese                text,  -- III: relatos do responsável
    estado_geral            text,  -- IV: observações sobre o estado do animal
    exame_clinico           text,  -- V: achados do exame e dos laboratoriais
    diagnostico_presuntivo  text,  -- VI
    diagnostico_conclusivo  text,  -- VII: "quando houver" é do texto da norma
    conduta                 text,  -- o que fazer daqui em diante

    -- ── Art. 9º, IV — parâmetros MENSURADOS ──────────────────────────────
    --
    -- Todos opcionais: a norma manda registrar o que foi medido, não medir
    -- tudo em toda consulta. Numérico e não texto porque peso vira gráfico.
    peso_kg                 numeric(6,3),
    temperatura_c           numeric(4,1),
    frequencia_cardiaca     integer,
    frequencia_respiratoria integer,

    -- ── Rascunho e fechamento ────────────────────────────────────────────
    --
    -- Nulo = rascunho, e rascunho aceita estar incompleto: ela escreve durante
    -- a visita, em pedaços. Preenchido = fechado, e a partir daí só se corrige
    -- por retificação.
    --
    -- **A imutabilidade é regra nossa, não do CFMV.** A norma não tem conceito
    -- de fechar prontuário — pede evolução diária, que é registro que cresce.
    -- Escolhemos travar porque documento clínico que se reescreve em silêncio
    -- não vale como documento.
    concluido_em timestamptz,

    criado_em     timestamptz not null default now(),
    atualizado_em timestamptz not null default now(),

    -- É aqui que a obrigatoriedade dos campos vive, e não em `not null`.
    -- `not null` impediria salvar o rascunho pela metade, que é justamente
    -- como a nota clínica é escrita. O que precisa estar completo é o
    -- atendimento CONCLUÍDO — e o banco garante que nenhum se feche sem isso.
    constraint atendimento_concluido_esta_completo
        check (
            concluido_em is null
            or (
                length(trim(coalesce(estado_geral, ''))) > 0
                and length(trim(coalesce(diagnostico_presuntivo, ''))) > 0
            )
        ),

    constraint atendimento_local_nao_vazio
        check (length(trim(local_atendimento)) > 0),
    constraint atendimento_crmv_nao_vazio
        check (length(trim(crmv_veterinario)) > 0),
    constraint atendimento_peso_positivo
        check (peso_kg is null or peso_kg > 0),
    constraint atendimento_temperatura_plausivel
        check (temperatura_c is null or temperatura_c between 20 and 45),
    constraint atendimento_frequencias_positivas
        check (
            (frequencia_cardiaca is null or frequencia_cardiaca > 0)
            and (frequencia_respiratoria is null or frequencia_respiratoria > 0)
        )
);

-- "O histórico da Mel", do mais recente para o mais antigo — a leitura da
-- ficha do animal.
create index atendimento_por_animal on prontuario.atendimento (id_animal, criado_em desc);

comment on table  prontuario.atendimento is 'Registro clínico de um atendimento. Res. CFMV 1.321/2020 Art. 9º; guarda mínima de 5 anos após o último atendimento';
comment on column prontuario.atendimento.crmv_veterinario is 'Cópia, não referência: o documento antigo diz o CRMV da época';
comment on column prontuario.atendimento.concluido_em is 'Nulo = rascunho. Preenchido, o registro é imutável e só se corrige por retificação';


-- ── Art. 9º, VIII — procedimentos realizados ─────────────────────────────
--
-- Uma tabela de procedimento, e não uma de cirurgia: cirurgia é um
-- procedimento com nome próprio. Tabela por tipo faria nascer `cirurgia`,
-- `exame`, `curativo`, todas com as mesmas colunas.
--
-- A norma pede identificação do profissional POR PROCEDIMENTO, no plural —
-- numa cirurgia há cirurgião e anestesista. Aqui costuma ser uma pessoa só,
-- mas a coluna existe para o dia em que não for.
create table prontuario.procedimento (
    id_procedimento uuid primary key default gen_random_uuid(),

    -- `on delete cascade` vale só para o rascunho descartado: atendimento
    -- concluído não é apagado.
    id_atendimento  uuid not null
        references prontuario.atendimento (id_atendimento) on delete cascade,

    descricao         text not null,
    realizado_em      timestamptz not null,
    nome_profissional text not null,
    crmv_profissional text not null,

    criado_em timestamptz not null default now(),

    constraint procedimento_descricao_nao_vazia
        check (length(trim(descricao)) > 0)
);

create index procedimento_por_atendimento on prontuario.procedimento (id_atendimento);


-- ── Art. 9º, IX — imunizações ────────────────────────────────────────────
--
-- Presa ao ANIMAL, e só opcionalmente ao atendimento. Nem toda vacina do
-- histórico foi aplicada por ela: a primeira dose costuma vir da protetora ou
-- do tutor anterior, e uma tabela que só aceita o que ela aplicou perderia
-- metade da carteira de vacinação — que é justamente o documento que alguém
-- vai querer consultar.
create table prontuario.imunizacao (
    id_imunizacao uuid primary key default gen_random_uuid(),

    id_animal      uuid not null references cliente.animal (id_animal),
    id_atendimento uuid references prontuario.atendimento (id_atendimento),

    nome_vacina text not null,
    fabricante  text,
    lote        text,
    validade    date,

    data_aplicacao        date not null,
    proxima_dose_prevista date,

    -- Quem aplicou, quando foi ela. Nulo quando o registro é histórico
    -- informado pelo tutor — e aí o prontuário não mente sobre autoria.
    nome_veterinario text,
    crmv_veterinario text,

    criado_em     timestamptz not null default now(),
    atualizado_em timestamptz not null default now(),

    constraint imunizacao_nome_nao_vazio
        check (length(trim(nome_vacina)) > 0),
    constraint imunizacao_proxima_dose_depois
        check (proxima_dose_prevista is null or proxima_dose_prevista >= data_aplicacao),
    -- Vacina ligada a um atendimento foi aplicada por ela, e aí tem autor.
    constraint imunizacao_do_atendimento_tem_autor
        check (id_atendimento is null or crmv_veterinario is not null)
);

create index imunizacao_por_animal on prontuario.imunizacao (id_animal, data_aplicacao desc);

comment on column prontuario.imunizacao.id_atendimento is 'Nulo quando a vacina é histórico informado pelo tutor, aplicada por outro profissional';


-- ── Correção de atendimento fechado ──────────────────────────────────────
--
-- O registro original NUNCA é tocado. É isso que faz o prontuário valer como
-- documento: quem lê vê o que foi escrito na hora e o que foi corrigido
-- depois, com data e autor de cada um.
create table prontuario.retificacao (
    id_retificacao uuid primary key default gen_random_uuid(),

    id_atendimento uuid not null references prontuario.atendimento (id_atendimento),

    texto  text not null,
    motivo text not null,

    id_veterinario   uuid not null references acesso.usuario (id_usuario),
    nome_veterinario text not null,
    crmv_veterinario text not null,

    criado_em timestamptz not null default now(),

    constraint retificacao_texto_nao_vazio
        check (length(trim(texto)) > 0),
    constraint retificacao_motivo_nao_vazio
        check (length(trim(motivo)) > 0)
);

create index retificacao_por_atendimento on prontuario.retificacao (id_atendimento, criado_em);

comment on table prontuario.retificacao is 'Correção de atendimento já concluído. Acrescenta; nunca sobrescreve o original';
