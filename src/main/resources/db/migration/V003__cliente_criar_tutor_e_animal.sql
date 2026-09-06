-- Domínio CLIENTE — as pessoas e os bichos atendidos.
--
-- Base de todo o resto: `agendamento` e `prontuario` referenciam tutor e
-- animal. Por isso vem antes deles, mesmo a Agenda tendo sido desenhada
-- primeiro.

create schema if not exists cliente;

create table cliente.tutor (
    id_tutor          uuid primary key default gen_random_uuid(),
    nome_completo     text        not null,
    -- O contato do projeto inteiro passa por WhatsApp; por isso o telefone é
    -- obrigatório e o e-mail não.
    telefone_whatsapp text        not null,
    email             text,

    -- DESVIO DELIBERADO do padrao-nomenclatura.md, que previa uma tabela
    -- `endereco_tutor`. O endereço é inline porque:
    --   1. no MVP cada tutor tem um endereço;
    --   2. o histórico não depende dele — a consulta guarda o próprio
    --      `endereco_atendimento` em texto (ver o exemplo no §3 do padrão),
    --      então o endereço do tutor é só o valor padrão de novas consultas;
    --   3. tabela separada para uma linha garantida é join sem contrapartida.
    -- Vira tabela no dia em que um tutor precisar de um segundo endereço.
    cep               text,
    logradouro        text,
    numero            text,
    complemento       text,
    bairro            text,
    cidade            text,
    uf                text,

    observacoes       text,
    -- Sem exclusão física, como usuário, consulta e prontuário (ver CLAUDE.md).
    ativo             boolean     not null default true,
    criado_em         timestamptz not null default now(),
    atualizado_em     timestamptz not null default now(),

    constraint tutor_nome_completo_nao_vazio
        check (length(trim(nome_completo)) > 0),
    constraint tutor_telefone_nao_vazio
        check (length(trim(telefone_whatsapp)) > 0),
    constraint tutor_uf_valida
        check (uf is null or uf ~ '^[A-Z]{2}$')
);

-- Não há unicidade de e-mail nem de telefone: dois moradores da mesma casa
-- podem compartilhar o número, e o e-mail é opcional. Duplicata de pessoa se
-- resolve na busca da tela, não com constraint que trava um caso legítimo.
create index tutor_nome_completo_busca
    on cliente.tutor (lower(nome_completo));

create table cliente.animal (
    id_animal              uuid primary key default gen_random_uuid(),
    -- FK de verdade: tutor e animal vivem no mesmo schema. A regra de não
    -- atravessar domínio vale entre `cliente` e `agendamento`, não aqui.
    id_tutor               uuid        not null references cliente.tutor (id_tutor),
    nome_animal            text        not null,
    especie                text        not null,
    raca                   text,
    sexo_animal            text        not null,
    -- Opcional e frequentemente estimada: animal resgatado costuma não ter
    -- data conhecida. Campo obrigatório aqui viraria data inventada.
    data_nascimento_animal date,
    castrado               boolean,
    observacoes            text,

    -- Só dois estados, e de propósito.
    --
    -- Houve um `INATIVO` aqui, para "saiu da clientela". Foi removido: ausência
    -- não é estado que se declara, é fato que se observa — a data da última
    -- consulta já diz há quanto tempo o animal não aparece. E não existe o dia
    -- em que se decide "esse não volta mais", então o campo nunca seria
    -- marcado; um campo que nunca é marcado faz 'ATIVO' não significar nada.
    --
    -- Óbito é diferente por ser evento: tem um dia, a veterinária fica sabendo,
    -- e a tela precisa parar de falar do bichinho como se estivesse vivo.
    --
    -- Cadastro feito por engano não vira status: é excluído de verdade
    -- (DELETE /api/animais/{id}), possível só enquanto não houver histórico.
    situacao_animal        text        not null default 'ATIVO',

    criado_em              timestamptz not null default now(),
    atualizado_em          timestamptz not null default now(),

    constraint animal_nome_nao_vazio
        check (length(trim(nome_animal)) > 0),
    -- Espécie é fechada: a clínica é de cães e gatos (ver CLAUDE.md). Abrir
    -- para outras espécies é decisão de negócio, e vai exigir migration.
    constraint animal_especie_valida
        check (especie in ('CAO', 'GATO')),
    constraint animal_sexo_valido
        check (sexo_animal in ('MACHO', 'FEMEA', 'INDEFINIDO')),
    constraint animal_situacao_valida
        check (situacao_animal in ('ATIVO', 'OBITO')),
    -- Nascimento no futuro é erro de digitação, não dado.
    constraint animal_nascimento_no_passado
        check (data_nascimento_animal is null or data_nascimento_animal <= current_date)
);

create index animal_por_tutor on cliente.animal (id_tutor);
create index animal_nome_busca on cliente.animal (lower(nome_animal));

comment on table  cliente.tutor is 'Pessoa responsável por um ou mais animais';
comment on table  cliente.animal is 'Animal atendido; pertence a exatamente um tutor';
comment on column cliente.tutor.ativo is 'Inativação lógica; não há exclusão física';
comment on column cliente.animal.situacao_animal is 'ATIVO ou OBITO. Cadastro errado é excluído, não inativado';
comment on column cliente.animal.data_nascimento_animal is 'Opcional: animal resgatado costuma não ter data conhecida';
