package com.vetplanet.common.dto;

import java.util.List;

/**
 * Uma fatia de uma lista, com o suficiente para a tela montar a navegação.
 *
 * <p>Fica em {@code common} porque é vocabulário compartilhado e não cita
 * nenhum conceito da clínica — a regra do {@code CLAUDE.md} é justamente essa:
 * se uma classe aqui falasse de {@code Animal} ou {@code Consulta}, o lugar
 * dela seria dentro do domínio.
 *
 * <p><b>Devolve {@code totalDeItens} e não só {@code temProxima}.</b> A tela
 * precisa dizer "12 de 87" e desenhar o número de páginas; sem o total, a
 * navegação vira apenas "avançar até acabar", que esconde o tamanho do que se
 * está percorrendo.
 *
 * <p><b>{@code pagina} é base 1</b>, como a pessoa conta. A conversão para o
 * índice base 0 acontece uma vez, no serviço — e não espalhada por tela e API,
 * que é onde o erro de um a mais nasce.
 *
 * @param itens os desta página
 * @param pagina qual é esta, começando em 1
 * @param tamanhoDaPagina quantos cabem por página
 * @param totalDeItens quantos existem ao todo, considerando os filtros
 */
public record PaginaDto<T>(List<T> itens, int pagina, int tamanhoDaPagina, long totalDeItens) {

    /** Quantas páginas o total ocupa. Lista vazia é uma página vazia, não zero. */
    public int totalDePaginas() {
        if (tamanhoDaPagina <= 0) return 1;
        return Math.max(1, (int) Math.ceil((double) totalDeItens / tamanhoDaPagina));
    }

    public boolean temProxima() {
        return pagina < totalDePaginas();
    }

    public boolean temAnterior() {
        return pagina > 1;
    }

    /**
     * Recorta a página de uma lista já ordenada.
     *
     * <p>Existe porque as listas de animais e tutores são ordenadas por dado de
     * outro módulo — o último atendimento vem de {@code agendamento} — e isso
     * não cabe num {@code order by} do banco sem furar a fronteira entre
     * schemas. Ordena-se em memória, e o recorte acontece aqui.
     *
     * <p><b>É adequado à escala de hoje e não à de sempre:</b> um veterinário
     * autônomo tem centenas de animais, não milhões. Quando deixar de ser
     * verdade, a saída é guardar a data do último atendimento no próprio
     * cadastro e paginar no banco — não é ajustar este método.
     */
    public static <T> PaginaDto<T> recortar(List<T> ordenados, int pagina, int tamanhoDaPagina) {
        int paginaSegura = Math.max(1, pagina);
        int tamanhoSeguro = Math.max(1, tamanhoDaPagina);

        int inicio = Math.min((paginaSegura - 1) * tamanhoSeguro, ordenados.size());
        int fim = Math.min(inicio + tamanhoSeguro, ordenados.size());

        return new PaginaDto<>(
                List.copyOf(ordenados.subList(inicio, fim)),
                paginaSegura,
                tamanhoSeguro,
                ordenados.size());
    }
}
