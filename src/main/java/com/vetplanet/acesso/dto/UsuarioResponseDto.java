package com.vetplanet.acesso.dto;

import com.vetplanet.acesso.entity.PerfilAcesso;
import com.vetplanet.acesso.entity.UsuarioEntity;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Usuário como sai da API.
 *
 * <p><b>Nunca inclua {@code senhaHash} aqui.</b> Este record existe
 * justamente para que a entidade não seja serializada direto — assim é
 * impossível o hash vazar por descuido numa resposta.
 */
public record UsuarioResponseDto(
        UUID idUsuario,
        String nomeCompleto,
        String email,
        PerfilAcesso perfilAcesso,
        /** Nulo para quem não é veterinário. */
        String crmv,
        boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm) {

    public static UsuarioResponseDto de(UsuarioEntity usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getPerfilAcesso(),
                usuario.getCrmv(),
                usuario.isAtivo(),
                usuario.getCriadoEm(),
                usuario.getAtualizadoEm());
    }
}
