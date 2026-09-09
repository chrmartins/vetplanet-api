-- Os dados de contato que vão no rodapé do receituário.
--
-- O receituário é papel timbrado, e o timbre é **de quem assina**, não do
-- produto: telefone, Instagram, site e cidade mudam a cada assinante. Chumbar
-- os da primeira veterinária faria o segundo assinante imprimir receita com o
-- contato dela.
--
-- **Não há logo do assinante, e isso é decisão.** O único símbolo no documento
-- é o do VetPlanet, pequeno; o destaque é o nome do profissional, composto na
-- tipografia do produto. Isso mantém o receituário inteiramente construído a
-- partir de texto — sem upload, sem armazenamento de arquivo, sem decidir onde
-- imagens moram e o que acontece com elas quando alguém cancela a assinatura.
--
-- Todos opcionais: são dados de vitrine, não de identificação. Um veterinário
-- sem Instagram não deve ser impedido de emitir receita, e receituário sem
-- telefone continua sendo receituário — o que a norma exige (nome e CRMV) já
-- é obrigatório em outro lugar.

alter table acesso.usuario
    add column telefone_contato text,
    add column instagram        text,
    add column site             text,
    add column cidade_atuacao   text;

comment on column acesso.usuario.telefone_contato is 'Aparece no rodapé do receituário';
comment on column acesso.usuario.instagram is 'Perfil no Instagram, com ou sem @ — a exibição normaliza';
comment on column acesso.usuario.site is 'Endereço do site, sem protocolo';
comment on column acesso.usuario.cidade_atuacao is 'Cidade e UF onde atende, como aparece no rodapé';
