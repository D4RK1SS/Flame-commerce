package com.flamecommerce.service;

import com.flamecommerce.dto.CadastroForm;
import com.flamecommerce.exception.RegraNegocioException;
import com.flamecommerce.model.Carrinho;
import com.flamecommerce.model.Perfil;
import com.flamecommerce.model.Usuario;
import com.flamecommerce.repository.CarrinhoRepository;
import com.flamecommerce.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          CarrinhoRepository carrinhoRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** RN1: todo cliente que compra precisa estar cadastrado. A senha é guardada com hash BCrypt. */
    @Transactional
    public Usuario cadastrar(CadastroForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("Este e-mail já está cadastrado.");
        }
        Usuario usuario = usuarioRepository.save(
                new Usuario(form.getNome().trim(), email, passwordEncoder.encode(form.getSenha()), Perfil.CLIENTE));
        carrinhoRepository.save(new Carrinho(usuario));
        return usuario;
    }
}
