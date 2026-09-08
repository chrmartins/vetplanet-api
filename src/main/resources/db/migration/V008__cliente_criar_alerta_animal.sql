-- Alertas do animal — o que não pode passar despercebido.
--
-- "Alergia à dipirona", "Diabética", "Cardiopata". São fatos que precisam
-- estar diante dos olhos no momento em que ela decide a conduta, não a dois
-- cliques de distância.
--
-- **Tabela, e não mais um campo de texto em `animal`.** Já existe
-- `animal.observacoes`, e alergia escrita ali se perde: observação é parágrafo
-- que se lê na diagonal, alerta é etiqueta que salta. A diferença aparece na
-- tela — cada linha desta tabela vira um selo ao lado do nome do bicho — e é
-- ela que faz o alerta cumprir a função de alertar.

create table cliente.alerta_animal (
    id_alerta  uuid primary key default gen_random_uuid(),

    -- `on delete cascade` porque alerta não é histórico: é atributo do
    -- cadastro. Some junto com o animal nos únicos casos em que o animal pode
    -- ser apagado — cadastro criado por engano, sem consulta nenhuma.
    id_animal  uuid not null references cliente.animal (id_animal) on delete cascade,

    -- Texto livre e curto. Catálogo de alergias exigiria manter uma lista de
    -- princípios ativos atualizada, e o que ela precisa é escrever o que viu.
    texto      text not null,

    criado_em     timestamptz not null default now(),
    atualizado_em timestamptz not null default now(),

    constraint alerta_texto_nao_vazio
        check (length(trim(texto)) > 0),
    -- Curto de propósito: alerta que não cabe numa etiqueta não é alerta, é
    -- observação — e para isso já existe `animal.observacoes`.
    constraint alerta_texto_curto
        check (length(texto) <= 60)
);

-- Sempre lido junto com o animal: "os alertas da Mel".
create index alerta_por_animal on cliente.alerta_animal (id_animal);

-- O mesmo alerta duas vezes no mesmo bicho é engano de digitação, não dado.
create unique index alerta_unico_por_animal
    on cliente.alerta_animal (id_animal, lower(texto));

comment on table cliente.alerta_animal is 'Etiquetas que aparecem ao lado do nome do animal: alergia, comorbidade. Distinto de animal.observacoes, que é texto corrido';
