package com.vetplanet.acesso.service;

import com.vetplanet.acesso.dto.CriarUsuarioRequestDto;
import com.vetplanet.acesso.dto.UsuarioResponseDto;
import com.vetplanet.acesso.entity.UsuarioEntity;
import com.vetplanet.acesso.exception.EmailJaCadastradoException;
import com.vetplanet.acesso.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastra um usuário do painel. */
@Service
public class CriarUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CriarUsuarioService(
            UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDto criarUsuario(CriarUsuarioRequestDto request) {
        // Checagem antecipada para devolver 409 com mensagem clara. O índice
        // único do banco continua sendo a garantia real contra corrida entre
        // dois cadastros simultâneos.
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        UsuarioEntity usuario =
                UsuarioEntity.criar(
                        request.nomeCompleto(),
                        request.email(),
                        passwordEncoder.encode(request.senha()),
                        request.perfilAcesso());

        return UsuarioResponseDto.de(usuarioRepository.save(usuario));
    }
}
