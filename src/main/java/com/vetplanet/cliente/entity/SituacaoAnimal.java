package com.vetplanet.cliente.entity;

/**
 * O animal está vivo ou faleceu.
 *
 * <p><b>Não existe "inativo".</b> Houve, para "saiu da clientela", e foi
 * removido: ausência não é estado que se declara, é fato que se observa — a
 * data da última consulta já diz há quanto tempo o animal não aparece. Não
 * existe o dia em que se decide "esse não volta mais", então o campo nunca
 * seria marcado, e campo que nunca é marcado faz {@code ATIVO} não significar
 * nada. Um animal pode passar cinco anos sem consulta e voltar; basta continuar
 * no cadastro.
 *
 * <p>Óbito ganha o lugar dele por ser <b>evento</b>: tem um dia, a veterinária
 * fica sabendo, e a tela precisa parar de falar do bichinho como se estivesse
 * vivo.
 *
 * <p>Cadastro feito por engano não é status — é exclusão de verdade. Ver
 * {@code ExcluirAnimalService}.
 */
public enum SituacaoAnimal {
    ATIVO,
    OBITO
}
