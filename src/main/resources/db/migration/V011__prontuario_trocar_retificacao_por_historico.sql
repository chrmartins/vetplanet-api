-- O prontuário deixa de travar; passa a guardar rastro.
--
-- A V009 previa `retificacao`: atendimento concluído virava imutável, e
-- corrigir exigia registrar uma correção com motivo. Era regra nossa, não do
-- CFMV — a norma não tem conceito de fechar prontuário; ela pede "evolução
-- diária" (Art. 9º, VIII), que é registro que CRESCE.
--
-- O que derrubou a regra foi o uso real: a veterinária lembra do nódulo na
-- pata depois de sair da casa, ou a tutora conta algo no portão. Travar isso
-- não protege o documento — empobrece o prontuário, que é o oposto do que a
-- imutabilidade queria fazer.
--
-- **O que dá valor ao documento não é a impossibilidade de editar; é o
-- rastro.** Um prontuário que se reescreve em silêncio não prova nada; um que
-- registra quem mudou o quê e quando prova tudo — e ainda deixa acrescentar.
-- Por isso a tabela de retificação, que exigia cerimônia dela, dá lugar a um
-- histórico gravado sozinho.

drop table prontuario.retificacao;

create table prontuario.alteracao_atendimento (
    id_alteracao   uuid primary key default gen_random_uuid(),

    id_atendimento uuid not null
        references prontuario.atendimento (id_atendimento) on delete cascade,

    -- Quem alterou, com nome e CRMV copiados pela mesma razão do atendimento:
    -- o rastro tem de dizer o que valia na época, não o que vale hoje.
    id_veterinario   uuid not null references acesso.usuario (id_usuario),
    nome_veterinario text not null,
    crmv_veterinario text not null,

    -- Uma linha por campo alterado, e não um retrato inteiro por salvamento.
    -- Assim "o que mudou nesse atendimento?" é uma leitura, não um diff de
    -- dois JSONs — e ela salva a cada campo que perde o foco, então retratos
    -- inteiros encheriam a tabela de repetição.
    campo          text not null,
    valor_anterior text,
    valor_novo     text,

    criado_em timestamptz not null default now(),

    constraint alteracao_campo_nao_vazio
        check (length(trim(campo)) > 0),
    -- Alteração que não muda nada não é alteração. Evita gravar linha a cada
    -- blur de campo que a pessoa só visitou.
    constraint alteracao_mudou_algo
        check (valor_anterior is distinct from valor_novo)
);

create index alteracao_por_atendimento
    on prontuario.alteracao_atendimento (id_atendimento, criado_em desc);

comment on table prontuario.alteracao_atendimento is 'Rastro de quem mudou o quê no registro clínico. Substitui a imutabilidade: o prontuário é sempre editável, e toda mudança fica registrada';
comment on column prontuario.alteracao_atendimento.campo is 'Nome do campo alterado, como na entidade — anamnese, exameClinico, pesoKg...';


-- `concluido_em` fica, mas muda de significado: era cadeado, vira MARCO.
--
-- Continua sendo o que conclui a consulta — "escrever é terminar" segue
-- valendo — e continua exigindo exame e diagnóstico preenchidos, porque
-- atendimento dado por terminado sem eles estaria incompleto perante a norma.
-- O que ele deixa de fazer é impedir a edição.
comment on column prontuario.atendimento.concluido_em is 'Quando ela deu o atendimento por terminado; conclui a consulta. NÃO trava a edição — o registro segue editável, e toda mudança vai para alteracao_atendimento';
