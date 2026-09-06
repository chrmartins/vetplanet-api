# Prontuário — o que precisa entrar, e o que ainda é decisão

> **Documento de proposta, não de regra vigente.** Nada aqui foi implementado.
> Serve para a conversa que precisa acontecer antes de codar. As regras que
> forem aceitas mudam de lugar: vão para a seção "Regras de negócio" do
> `CLAUDE.md`, que é onde a regra vigente mora.
>
> Escrito em 2026-09-06.

## Por que este documento existe

O `CLAUDE.md` diz três coisas sobre prontuário e nenhuma delas basta para
começar:

- prontuário é dado pessoal sensível (LGPD);
- é imutável após confirmado — correção gera retificação;
- faz parte do MVP.

Falta o essencial: **o que ele contém**. E, ao contrário dos outros domínios,
aqui não dá para escolher livremente — o prontuário veterinário é documento
técnico-legal com conteúdo obrigatório definido em resolução do CFMV.

## O que a lei exige

Fonte: **Resolução CFMV nº 1.653, de 26 de junho de 2025**, que atualiza a
1.321/2020.

### Confirmado

Por atendimento, o prontuário precisa registrar:

| Exigência | O que significa para o sistema |
|---|---|
| Data, hora e local do atendimento | Já temos na consulta. **Local** é o campo `endereco_atendimento`, hoje opcional — vira obrigatório no prontuário |
| Identificação do profissional, **por nome e número de CRMV** | Não temos. `acesso.usuario` não tem CRMV |
| Relatos e informações prestados pelo tutor | Anamnese |
| Estado geral do animal e **parâmetros mensurados** | Peso, temperatura, frequências |
| Achados de histórico, anamnese, exame clínico e laboratorial | O corpo do registro clínico |
| Informações sobre **imunizações** | Vacinas aplicadas, com dados do produto |

E, sobre a cópia ao tutor: **5 dias úteis** para entregar quando solicitada,
prorrogáveis até 30 dias com justificativa.

### A confirmar no texto oficial

Dois pontos apareceram em fontes secundárias e **não** foram confirmados no
texto da resolução. Os dois têm consequência grande, e é por isso que estão
separados:

- **Guarda mínima de 20 anos.**
- **Assinatura digital qualificada (ICP-Brasil)** quando o prontuário é
  guardado em meio eletrônico.

O segundo é o que mais pesa: se for exigência mesmo, muda arquitetura, custo e
prazo — certificado por veterinário, fluxo de assinatura e provavelmente um
serviço de terceiro. **Antes de escrever qualquer linha do domínio, alguém
precisa ler a resolução e responder isso.** Construir e descobrir depois seria
refazer.

## Proposta de modelo

Schema `prontuario`, seguindo a regra de um schema por domínio.

### `prontuario.atendimento` — o registro de um atendimento

Um por consulta. É o núcleo, e a estrutura segue o SOAP, que é como o registro
clínico é ensinado e escrito:

| Coluna | Papel |
|---|---|
| `id_consulta` | FK para `agendamento.consulta` — a âncora |
| `id_animal` | redundante com a consulta, mas é o que sustenta "o histórico da Mel" sem passar por agendamento |
| `id_veterinario`, `crmv_veterinario` | quem assina. **CRMV como cópia**, não como referência: se o número mudar no cadastro, o registro antigo continua dizendo o que valia na época — mesma razão do `id_tutor` na consulta |
| `local_atendimento` | exigência legal; copiado da consulta no momento de abrir |
| `anamnese` | *S* — o que o tutor relatou |
| `exame_clinico` | *O* — o que ela observou |
| `avaliacao` | *A* — suspeitas e diagnóstico |
| `conduta` | *P* — o que foi feito e o que fazer |
| `peso_kg`, `temperatura_c`, `freq_cardiaca`, `freq_respiratoria` | os parâmetros mensurados |
| `confirmado_em` | nulo = rascunho. Depois de preenchido, o registro é imutável |

**Parâmetros em colunas fixas, e não numa tabela genérica de medições.** São
poucos e estáveis, e a coluna é o que faz "evolução de peso" ser um gráfico
simples em vez de uma agregação sobre pares chave-valor. Se um dia houver
medição fora dessa lista, ela entra como coluna nova — barato.

### `prontuario.imunizacao` — vacinas aplicadas

Exigência explícita da resolução, e o dado que ninguém consegue reconstruir
depois: `nome_vacina`, `fabricante`, `lote`, `validade`, `data_aplicacao`,
`proxima_dose_prevista`.

**Presa ao animal, e opcionalmente ao atendimento.** Nem toda vacina do
histórico foi aplicada por ela: a primeira dose costuma vir da protetora ou do
tutor anterior, e um cadastro que só aceita o que ela mesma aplicou perde
metade da carteira de vacinação.

### `prontuario.procedimento` — o que foi feito

Cirurgia, castração, limpeza dentária, curativo. Preso ao atendimento.

**Uma tabela de procedimento, e não uma tabela de cirurgia.** Cirurgia não é
uma categoria diferente de registro — é um procedimento com nome próprio.
Tabela por tipo faria nascer `cirurgia`, `exame`, `curativo`, todas com as
mesmas colunas.

### `prontuario.retificacao` — a correção

O `CLAUDE.md` já manda: confirmado não se edita, corrige-se com retificação. A
tabela guarda `id_atendimento`, o texto da correção, o motivo, quem fez e
quando. **O registro original nunca é tocado** — é isso que faz o prontuário
valer como documento.

### Fora desta proposta: receituário

Receita veterinária é documento próprio, com regulamentação própria (a receita
digital tem exigências separadas). Meter prescrição dentro do atendimento
agora seria começar um segundo domínio sem perceber. Proponho tratar como fase
seguinte.

## O que precisa ser decidido antes de codar

Estas são perguntas de negócio, não de implementação. Não vou assumir nenhuma.

1. **Assinatura digital ICP-Brasil é exigida?** Se sim: implementar agora,
   assinar em papel por fora, ou registrar como não assinado e assumir o risco?
   É a decisão que mais muda o tamanho do trabalho.
2. **Quem pode escrever um prontuário?** A resolução exige nome e CRMV do
   profissional. Hoje `PerfilAcesso` tem `ATENDENTE`, e o administrador inicial
   não é necessariamente veterinário. Proponho: **só `VETERINARIO` assina**, e
   `acesso.usuario` ganha um campo CRMV obrigatório para esse perfil.
3. **Quando o atendimento vira imutável?** Ao salvar, ou existe rascunho que
   ela fecha depois? Um prontuário que trava no primeiro salvamento vai
   produzir retificação para erro de digitação.
4. **A cópia ao tutor em 5 dias é feature ou processo manual?** Se é feature,
   é exportação em PDF — e aí precisa layout, cabeçalho com CRMV, e a decisão
   de o que entra na cópia.
5. **Guarda de 20 anos** muda a política de backup. O `vetplanet-docs` hoje não
   tem backup fora da máquina; o banco de produção precisa ter, e isso é
   infraestrutura que ainda não existe.
6. **Peso e parâmetros são obrigatórios em todo atendimento?** "Parâmetros
   mensurados" sugere que sim, mas numa consulta de retorno rápido pode não
   fazer sentido pesar o bicho.

## O que já está pronto e serve de base

- `agendamento.consulta` é a âncora, e já tem data, hora, local e animal.
- A ficha do animal (`/painel/animais/[idAnimal]`) já é a tela do prontuário —
  hoje mostra identificação, tutor e histórico de consultas. Vacinas, peso e
  procedimentos entram nela.
- A regra de exclusão já está alinhada: consulta e prontuário nunca são
  apagados, e animal com histórico é recusado pelo banco.
