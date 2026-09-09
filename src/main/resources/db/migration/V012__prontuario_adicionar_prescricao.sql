-- A prescrição sai de dentro das recomendações e vira campo próprio.
--
-- O campo `recomendacoes` acumulava duas coisas de naturezas diferentes:
-- orientação clínica ("voltar em 15 dias, banho só depois da cicatrização") e
-- prescrição de medicamento ("dipirona 500mg, 1 comprimido de 12/12h por 5
-- dias"). Escrever as duas juntas é natural para quem fala; separar importa
-- porque **só uma delas vira receita**.
--
-- E receita tem forma: medicamento, concentração, dose, via, frequência e
-- duração. Antimicrobiano, no Brasil, ainda exige receituário próprio em duas
-- vias (IN MAPA 25/2011). Nada disso se cobra de um texto que também guarda
-- "não deixar subir no sofá".
--
-- **Continua sendo texto livre, e isso é deliberado.** Estruturar cada
-- medicamento em linhas com dose e via é o passo seguinte, e só vale quando
-- houver o documento que consome essa estrutura. Hoje o ganho é a separação:
-- o que é prescrição fica identificável, e o que é orientação para de ser
-- confundido com ela.
--
-- Opcional como o resto: atendimento sem medicamento é comum, e obrigar a
-- preencher produziria "nenhum" digitado mil vezes.

alter table prontuario.atendimento
    add column prescricao text;

comment on column prontuario.atendimento.prescricao is 'Medicamentos prescritos, em texto livre. Separado de recomendacoes porque só a prescrição vira receituário';
