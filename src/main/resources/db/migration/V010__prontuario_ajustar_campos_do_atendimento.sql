-- Quatro campos, não seis.
--
-- A V009 separava `estado_geral` (Art. 9º, IV) de `exame_clinico` (V), porque
-- a norma os lista em incisos distintos. Na prática o veterinário escreve os
-- dois no mesmo fôlego — "estado geral bom, mucosas hipocoradas" — e dois
-- campos vizinhos pedindo a mesma coisa fazem um deles ficar sempre vazio.
--
-- Campo vazio num documento legal é pior que campo inexistente: parece que
-- alguém deixou de registrar. Um campo só, que cobre os dois incisos, é mais
-- honesto e mais provável de ser preenchido.
--
-- A separação que continua valendo é outra, e é a que importa: **o que a
-- tutora relatou** (`anamnese`) fica longe de **o que a veterinária
-- constatou** (`exame_clinico`). Relato de terceiro e constatação profissional
-- não podem morar no mesmo parágrafo num documento que pode ser pedido numa
-- disputa.

alter table prontuario.atendimento
    drop constraint atendimento_concluido_esta_completo;

alter table prontuario.atendimento
    drop column estado_geral;

-- `conduta` vira `recomendacoes` porque é assim que a tela chama, e é assim
-- que ela fala. Vocabulário que diverge entre banco e tela é como nasce a
-- confusão que já custou caro neste projeto.
alter table prontuario.atendimento
    rename column conduta to recomendacoes;

-- Mesma regra de antes, sobre o campo que sobrou: o rascunho pode estar pela
-- metade, o atendimento CONCLUÍDO não pode.
alter table prontuario.atendimento
    add constraint atendimento_concluido_esta_completo
        check (
            concluido_em is null
            or (
                length(trim(coalesce(exame_clinico, ''))) > 0
                and length(trim(coalesce(diagnostico_presuntivo, ''))) > 0
            )
        );

comment on column prontuario.atendimento.anamnese is 'Art. 9º, III — o que o responsável relatou. Separado do exame de propósito: relato de terceiro não é constatação profissional';
comment on column prontuario.atendimento.exame_clinico is 'Art. 9º, IV e V — estado geral e achados. Um campo só: o veterinário escreve os dois juntos';
comment on column prontuario.atendimento.recomendacoes is 'O que fazer daqui em diante';
