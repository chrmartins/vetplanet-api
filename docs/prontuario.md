# Prontuário — o que a lei exige, e o que decidimos

> Atualizado em 2026-09-06, depois de **ler o texto oficial** da Resolução CFMV
> nº 1.321/2020 e da nº 1.653/2025, que a altera. As seis decisões que estavam
> em aberto estão respondidas ao final.
>
> Nada aqui foi implementado. O que for aceito migra para a seção "Regras de
> negócio" do `CLAUDE.md`, que é onde a regra vigente mora.

## Correção: duas coisas que a versão anterior deste documento dizia estavam erradas

A versão anterior marcava como "a confirmar" dois pontos que vieram de fontes
secundárias — blogs de fornecedores de software. **Os dois são falsos**, e a
verificação foi ler o PDF oficial e procurar os termos:

| O que se dizia | O que a resolução diz |
|---|---|
| Guarda mínima de **20 anos** | **5 anos** após a data do último atendimento, mesmo em caso de óbito (Art. 9º, §3º). As palavras "20 anos" e "vinte anos" não aparecem no texto |
| Exige **assinatura digital ICP-Brasil** para prontuário eletrônico | **Não exige.** "ICP", "certificado", "criptografia", "backup" e "auditoria" têm **zero ocorrências** na 1.321/2020 |

De onde saiu o "ICP-Brasil": a 1.653/2025 tem exatamente **uma** menção a ICP —
no **rodapé da página do Diário Oficial**, que é o boilerplate com que a
Imprensa Nacional assina os próprios PDFs:

> "Documento assinado digitalmente conforme MP nº 2.200-2 de 24/08/2001, que
> institui a Infraestrutura de Chaves Públicas Brasileira - ICP-Brasil."

Isso diz respeito ao PDF do DOU, não ao prontuário. Alguém leu o rodapé como
se fosse artigo, escreveu num blog, e a informação passou a circular.

**Lição para as próximas:** em regra profissional, ler a norma. Fonte
secundária de fornecedor tem interesse em fazer a exigência parecer maior do
que é — quanto mais assustadora a conformidade, mais necessário o produto.

## O que a lei exige, de fato

Fonte: **Resolução CFMV nº 1.321/2020**, Seção V (Art. 9º), com a redação dada
pela **Resolução nº 1.653/2025**.

### Conteúdo, por atendimento (Art. 9º)

| Inciso | Exigência | Situação no VetPlanet |
|---|---|---|
| I | Data, horário e **local** do atendimento | Data e hora já existem na consulta. `endereco_atendimento` é opcional hoje — **precisa ser obrigatório** no prontuário |
| II | Identificação do médico-veterinário atendente | **Não existe.** `acesso.usuario` não tem CRMV |
| III | Relatos e informações prestados pelo responsável | Anamnese |
| IV | Estado geral do animal e **parâmetros mensurados** | Peso, temperatura, frequências |
| V | Achados do histórico, anamnese, exame clínico e laboratorial | Exame clínico |
| VI | **Diagnóstico presuntivo** | Campo próprio |
| VII | **Diagnóstico conclusivo**, quando houver | Campo próprio, opcional — o "quando houver" é do texto |
| VIII | Procedimentos com **evolução diária**, data, hora e identificação dos profissionais (**nome completo e número de CRMV**) | Procedimentos |
| IX | Informações sobre imunizações | Vacinas |
| X | **Cópia impressa ou digitalizada de cada laudo** de exame laboratorial ou de imagem | **Não previsto.** Exige guardar arquivo — ver abaixo |

Dois pontos que a versão anterior deste documento não tinha:

**O inciso X é novo (1.653/2025) e traz escopo que ninguém contava:** o
prontuário precisa guardar **o arquivo** de cada laudo de exame. Isso é upload
e armazenamento de documento, com o que vem junto — tamanho, tipo, backup, e o
fato de que laudo de exame é dado sensível.

**Diagnóstico é dois campos, não um.** Presuntivo (VI) e conclusivo (VII) são
incisos separados, e o segundo é condicional. A proposta anterior tinha um
campo só, "avaliação".

### Guarda (Art. 9º, §3º)

> "O prontuário deve ser arquivado por pelo menos 5 anos após a data do último
> atendimento, mesmo em caso de óbito do animal."

Cinco anos é o **mínimo legal**. A regra do projeto — nunca excluir prontuário
— já é mais conservadora, e continua valendo.

### Cópia ao responsável (Art. 9º, §1º)

Aqui a norma é detalhada, e cria **requisito de funcionalidade**, não só de
dado:

- **5 dias úteis** contados do protocolo do pedido, prorrogáveis para até **30
  dias úteis** com justificativa **por escrito** (alíneas a e b);
- exames feitos por terceiros contratados pelo estabelecimento: até 30 dias
  úteis (alínea c);
- o pedido pode ser feito **pessoalmente ou por meio eletrônico**, desde que
  seja possível **conferir a identidade** de quem pede (alínea e);
- entrega **mediante comprovante de recebimento** (alínea e);
- só o responsável que consta no cadastro, ou alguém **expressamente
  autorizado** por ele (alínea f).

Note que "protocolo do pedido" e "comprovante de recebimento" pressupõem
**registro do pedido e da entrega** — não basta um botão de imprimir.

E o prontuário **não** é entregue por padrão: o Art. 3º, I manda emitir os
documentos em duas vias, mas **excetua expressamente** atestados sanitários,
prontuários e carteiras de vacinação. O prontuário só sai quando pedido.

### O que não se aplica a este produto

**Art. 11 — retirada sem alta médica**, com termo assinado e, em caso de
recusa, duas testemunhas. Pressupõe internação, e o VetPlanet é atendimento
domiciliar: não há de onde retirar o animal. Fica registrado para o dia em que
alguém propuser suportar clínica com internação.

**Art. 9º, §2º — extravio do prontuário** (comunicar o responsável, abrir novo,
boletim de ocorrência, avisar o CRMV). É processo, não software.

## Como o mercado resolve

O achado mais útil da pesquisa não foi sobre prontuário: **os produtos de
assinatura digital do ramo veterinário brasileiro são todos de receituário**,
não de prontuário. Vet Smart, Meu Receituário Digital e Guiavet PRO vendem
"prescrição eletrônica assinada"; nenhum vende "prontuário assinado".

Isso confirma, por outro caminho, o que o texto da resolução já dizia: a
assinatura importa no documento que **sai** e circula — a receita, que vale
para comprar medicamento — e não no registro interno.

Os sistemas de gestão (SimplesVet e semelhantes) tratam o prontuário como
histórico por paciente: consultas, exames, procedimentos, receitas, documentos,
fotos e vídeos. É a mesma forma que a ficha do animal já tem aqui.

**Consequência para nós:** receituário continua fora da fase 1, e quando entrar,
entra como documento próprio, com assinatura — não como campo do atendimento.

## As seis decisões, respondidas

### 1. Assinatura digital ICP-Brasil é exigida? **Não.**

Não construir. A exigência não existe na norma, e o mercado só assina receita.
Quando o receituário entrar, a assinatura entra com ele.

### 2. Quem pode escrever um prontuário? **Só veterinário, e com CRMV.**

O Art. 9º, II e VIII exige identificar o profissional por **nome completo e
número de CRMV**. Então:

- `acesso.usuario` ganha o campo `crmv`, **obrigatório para o perfil
  `VETERINARIO`** e vazio para os demais;
- só `VETERINARIO` pode criar e assinar um atendimento — `ATENDENTE` não;
- o CRMV é **copiado** para o registro, não referenciado: se o número mudar no
  cadastro, o atendimento antigo continua dizendo o que valia na época. Mesma
  razão do `id_tutor` na consulta;
- **atenção ao administrador**: o `ADMINISTRADOR` de hoje não é
  necessariamente veterinário. Perfil não é profissão, e quem administra o
  sistema não herda o direito de assinar prontuário.

### 3. Quando o atendimento vira imutável? **Nunca. A regra foi revista.**

A primeira resposta aqui era "ao concluir, fecha; depois só retificação", e
durou pouco. Caiu por um argumento melhor do dono do produto: *"posso ter
esquecido de mencionar algo importante sobre o paciente, e uma regra de
negócio me impediria de enriquecer o prontuário"*.

Ele está certo, e o enquadramento é melhor que o meu. Eu pensava em
**corrigir** — "escrevi 4,2 e era 4,5". Ele falava de **acrescentar** — o
nódulo na pata lembrado no carro, o que a tutora contou no portão. Travar isso
não protege o documento: empobrece.

E é o modelo do próprio CFMV. O Art. 9º, VIII pede **evolução diária** —
registro que cresce, não documento que fecha. A imutabilidade era invenção
nossa, e resolvia um problema que a norma não tem.

**O que ficou no lugar:** o prontuário é sempre editável, e cada campo
alterado depois da conclusão vira linha em `prontuario.alteracao_atendimento`,
com autor, CRMV, valor antigo e novo — gravada sozinha, sem formulário de
retificação. O que dá valor ao documento não é a impossibilidade de mudar; é o
rastro.

**Concluir virou marco**: diz quando o atendimento foi dado por terminado e é
o que conclui a consulta. Não trava nada.

### 4. A cópia ao tutor é funcionalidade. **Sim, e maior do que parece.**

Não é um botão de imprimir. A norma fala em **protocolo do pedido**,
**justificativa por escrito** quando estoura o prazo, **conferência de
identidade** de quem pede e **comprovante de recebimento**.

Proposta em duas etapas: primeiro a **exportação em PDF** do prontuário de um
animal (é o que destrava atender o pedido); depois o **registro de pedidos**,
com data de protocolo, prazo e entrega. A segunda etapa só faz sentido quando
houver volume — para uma veterinária autônoma, um pedido por ano se resolve
por fora, e o que ela não pode é não conseguir gerar o documento.

### 5. Guarda: **5 anos, não 20.** Não muda o desenho.

A regra de nunca excluir já cobre com folga. O que isso de fato exige é
**backup do banco de produção**, que ainda não existe — e isso é
infraestrutura, não código.

### 6. Parâmetros são obrigatórios em todo atendimento? **Não.**

O Art. 9º, IV pede "estado geral do animal e **parâmetros mensurados**" — os
que foram medidos. A norma não manda medir peso em toda consulta; manda
registrar o que se mediu.

Campos presentes, nenhum obrigatório. O que precisa ser obrigatório é o texto
de estado geral.

## Proposta de modelo, revisada

Schema `prontuario`, um por domínio como os demais.

### `prontuario.atendimento`

Um por consulta. Segue a ordem dos incisos do Art. 9º, que por acaso é o SOAP.

| Coluna | Inciso | Papel |
|---|---|---|
| `id_consulta` | I | FK para `agendamento.consulta` — data, hora e a âncora |
| `id_animal` | — | sustenta "o histórico da Mel" sem passar por agendamento |
| `id_veterinario`, `nome_veterinario`, `crmv_veterinario` | II, VIII | quem assina; nome e CRMV **copiados**, não referenciados |
| `local_atendimento` | I | exigência legal; copiado da consulta ao abrir |
| `anamnese` | III | o que o responsável relatou |
| `estado_geral` | IV | observações sobre o estado do animal |
| `peso_kg`, `temperatura_c`, `freq_cardiaca`, `freq_respiratoria` | IV | os parâmetros mensurados, todos opcionais |
| `exame_clinico` | V | achados do histórico, exame clínico e laboratorial |
| `diagnostico_presuntivo` | VI | obrigatório |
| `diagnostico_conclusivo` | VII | opcional — "quando houver" é do texto |
| `conduta` | — | o que fazer daqui em diante |
| `concluido_em` | — | nulo = rascunho; preenchido, só retificação |

Parâmetros em colunas fixas, não em tabela genérica de medições: são poucos e
estáveis, e a coluna é o que faz "evolução de peso" ser um gráfico simples em
vez de agregação sobre pares chave-valor.

### `prontuario.imunizacao` (Art. 9º, IX)

`nome_vacina`, `fabricante`, `lote`, `validade`, `data_aplicacao`,
`proxima_dose_prevista`.

Presa ao animal e **opcionalmente** ao atendimento: nem toda vacina do
histórico foi aplicada por ela — a primeira dose costuma vir da protetora ou do
tutor anterior, e um cadastro que só aceita o que ela aplicou perde metade da
carteira.

### `prontuario.procedimento` (Art. 9º, VIII)

`nome`, `descricao`, `data_hora`, e o profissional responsável. Preso ao
atendimento.

Uma tabela de procedimento, não uma de cirurgia: cirurgia é um procedimento com
nome próprio, e tabela por tipo faria nascer `cirurgia`, `exame`, `curativo`,
todas com as mesmas colunas.

### `prontuario.laudo` (Art. 9º, X) — **novo, e não trivial**

Cópia digitalizada de cada exame laboratorial ou de imagem. É a única parte que
exige **guardar arquivo**, e isso traz o que sempre traz: onde armazenar,
limite de tamanho, tipo aceito, e o fato de que laudo é dado pessoal sensível.

Vale decidir cedo se o arquivo vai para o banco, para disco, ou para
armazenamento de objeto — mudar depois é migração de dados.

### `prontuario.alteracao_atendimento`

Uma linha por campo alterado depois da conclusão: `id_atendimento`, autor com
nome e CRMV, `campo`, `valor_anterior`, `valor_novo`, `criado_em`.

Substituiu a tabela `retificacao`, que exigia dela escrever um motivo. O rastro
é gravado sozinho — ela edita como edita qualquer coisa, e o sistema anota.

Duas guardas que evitam transformar o rastro em ruído: o banco recusa linha em
que o valor não mudou, e o service normaliza `BigDecimal` com
`stripTrailingZeros` antes de comparar — sem isso, `4.500` vindo do banco e
`4.5` vindo da tela acusariam uma alteração de peso que não houve, a cada
salvamento.

### Fora da fase 1: receituário

Documento próprio, com regulamentação própria, e é onde a assinatura digital de
fato importa. Meter prescrição dentro do atendimento seria começar um segundo
domínio sem perceber.

## O que ainda precisa de você

As seis estão respondidas, mas três dessas respostas são propostas minhas e não
obrigações legais — vale confirmar antes de eu codar:

1. **Rascunho até concluir a consulta** é o momento certo de travar o
   atendimento? (decisão 3)
2. **Exportação em PDF agora, registro de pedidos depois** — concorda com a
   ordem? (decisão 4)
3. **Onde guardar os arquivos de laudo** — banco, disco ou objeto? É a decisão
   mais cara de mudar depois.
