package com.flamecommerce.config;

import com.flamecommerce.model.Carrinho;
import com.flamecommerce.model.Perfil;
import com.flamecommerce.model.Produto;
import com.flamecommerce.model.Usuario;
import com.flamecommerce.repository.CarrinhoRepository;
import com.flamecommerce.repository.ProdutoRepository;
import com.flamecommerce.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Dados de demonstração: roda só quando o banco está vazio. */
@Component
public class DataSeeder implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      CarrinhoRepository carrinhoRepository,
                      ProdutoRepository produtoRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() == 0) {
            criarUsuario("Administrador Flame", "admin@flame.com", "admin123", Perfil.ADMIN);
            criarUsuario("Cliente Demo", "cliente@flame.com", "cliente123", Perfil.CLIENTE);
        }
        if (produtoRepository.count() == 0) {
            produto("Fone Bluetooth Ember", "Cancelamento de ruído ativo e 40 horas de bateria.", "349.90", "Áudio", "🎧", 25);
            produto("Caixa de Som Spark", "À prova d'água, graves potentes e 12 horas de música.", "229.90", "Áudio", "🔊", 12);
            produto("Teclado Mecânico Inferno", "Switches vermelhos, iluminação RGB e layout ABNT2.", "459.00", "Periféricos", "⌨️", 8);
            produto("Mouse Gamer Blaze", "Sensor de 16.000 DPI e 6 botões programáveis.", "189.90", "Periféricos", "🖱️", 30);
            produto("Monitor 24\" Pyra", "Full HD, 144 Hz e 1 ms de resposta.", "1099.00", "Monitores", "🖥️", 5);
            produto("Webcam Full HD Aurora", "1080p com microfone duplo e correção de luz.", "279.90", "Periféricos", "📷", 3);
            produto("Camiseta Flame Dev", "100% algodão com estampa exclusiva.", "89.90", "Vestuário", "👕", 40);
            produto("Garrafa Térmica Magma", "Mantém bebidas quentes por 12 h e geladas por 24 h.", "119.90", "Casa", "🔥", 0);
            produto("Mochila Cinder", "Compartimento acolchoado para notebook de até 15,6\".", "249.90", "Acessórios", "🎒", 15);
            produto("Carregador Turbo 65W", "Carga rápida GaN com duas portas USB-C.", "159.90", "Acessórios", "🔌", 22);
        }
    }

    private void criarUsuario(String nome, String email, String senha, Perfil perfil) {
        Usuario usuario = usuarioRepository.save(new Usuario(nome, email, passwordEncoder.encode(senha), perfil));
        carrinhoRepository.save(new Carrinho(usuario));
    }

    private void produto(String nome, String descricao, String preco, String categoria, String emoji, int estoque) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setPreco(new BigDecimal(preco));
        p.setCategoria(categoria);
        p.setEmoji(emoji);
        p.definirEstoque(estoque);
        produtoRepository.save(p);
    }
}
