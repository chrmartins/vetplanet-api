-- CRMV no usuário — pré-requisito do prontuário.
--
-- A Resolução CFMV nº 1.321/2020, Art. 9º, incisos II e VIII, exige que o
-- prontuário identifique o profissional por NOME COMPLETO E NÚMERO DE CRMV.
-- Sem esta coluna não há como registrar um atendimento em conformidade.

alter table acesso.usuario
    add column crmv text;

-- **`NOT VALID` de propósito, e não é preguiça.**
--
-- A regra certa é "veterinário tem CRMV". Mas já existe veterinário cadastrado
-- sem o número, e as duas saídas óbvias são piores que esta:
--
--   - preencher com um valor qualquer escreveria CRMV falso num campo que é
--     identificação profissional em documento legal;
--   - deixar sem constraint permitiria cadastrar veterinário sem CRMV amanhã,
--     que é o problema que a coluna veio resolver.
--
-- `NOT VALID` faz a regra valer para toda linha nova e toda linha alterada, e
-- deixa as antigas em paz até alguém editá-las. Quando não houver mais
-- veterinário sem CRMV, roda-se:
--
--   alter table acesso.usuario validate constraint usuario_veterinario_tem_crmv;
--
-- e a regra passa a valer para tudo, sem migração nova.
alter table acesso.usuario
    add constraint usuario_veterinario_tem_crmv
        check (
            perfil_acesso <> 'VETERINARIO'
            or (crmv is not null and length(trim(crmv)) > 0)
        ) not valid;

-- Perfil não é profissão: administrador e atendente não têm CRMV, e preencher
-- para eles seria dizer que assinam prontuário — que não assinam.
alter table acesso.usuario
    add constraint usuario_crmv_so_de_veterinario
        check (perfil_acesso = 'VETERINARIO' or crmv is null) not valid;

comment on column acesso.usuario.crmv is 'Número de inscrição no CRMV. Obrigatório para VETERINARIO, nulo nos demais — Res. CFMV 1.321/2020, Art. 9º, II e VIII';
