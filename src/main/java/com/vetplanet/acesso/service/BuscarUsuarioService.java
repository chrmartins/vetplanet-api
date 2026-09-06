package com.vetplanet.acesso.service;

import com.vetplanet.acesso.dto.UsuarioResponseDto;
import com.vetplanet.acesso.exception.UsuarioNaoEncontradoException;
import com.vetplanet.acesso.repository.UsuarioRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Busca um usuário pelo identificador. */
@Service
public class BuscarUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public BuscarUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDto buscarUsuario(UUID idUsuario) {
        return usuarioRepository
                .findById(idUsuario)
                .map(UsuarioResponseDto::de)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(idUsuario));
    }
}
