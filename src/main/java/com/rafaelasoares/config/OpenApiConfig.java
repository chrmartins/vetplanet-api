package com.rafaelasoares.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadados da documentação OpenAPI.
 *
 * <p><b>Só existe quando a documentação está ligada</b> — o que, por padrão,
 * é apenas no perfil {@code dev} (ver {@code application.yml} e
 * {@code application-dev.yml}). Em produção este bean nem é criado.
 *
 * <p>Esta classe fica em {@code config/} porque descreve a API inteira, não
 * um domínio. Pela regra da seção "O que vive fora dos domínios" do
 * CLAUDE.md, ela não importa nada de {@code acesso} — e não deve passar a
 * importar quando os outros domínios chegarem. O que é específico de um
 * domínio (título de grupo, descrição de endpoint) é declarado com
 * {@code @Tag} e {@code @Operation} no controller dele.
 */
@Configuration
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true")
public class OpenApiConfig {

    /** Casa com o {@code Authorization: Bearer <token>} do filtro de acesso. */
    private static final String ESQUEMA_TOKEN = "token de sessão";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API — Dra. Rafaela Soares")
                                .version("v1")
                                .description(
                                        """
                                        Atendimento veterinário domiciliar. Consumida pelo \
                                        painel administrativo (Next.js).

                                        **Autenticação:** `POST /api/sessoes` devolve um token \
                                        opaco. Use o botão *Authorize* e cole só o token — o \
                                        prefixo `Bearer` é adicionado sozinho.

                                        O painel real não guarda esse token em JavaScript: \
                                        quem chama esta API é o servidor do Next, que o mantém \
                                        num cookie httpOnly (padrão BFF).

                                        **Erros** seguem sempre o mesmo envelope, com o \
                                        `requestId` que também volta no header `X-Request-Id` \
                                        — é por ele que se acha o rastro no log."""))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        ESQUEMA_TOKEN,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .description(
                                                        "Token devolvido por POST /api/sessoes."
                                                            + " Vale 12h e é revogado no"
                                                            + " logout.")))
                // Exigência padrão: quase tudo exige token. Os poucos endpoints
                // públicos (o próprio login) marcam a exceção com
                // @SecurityRequirements no controller.
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_TOKEN));
    }
}
