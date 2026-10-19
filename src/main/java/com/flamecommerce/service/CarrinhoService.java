package com.flamecommerce.service;

import com.flamecommerce.exception.RegraNegocioException;
import com.flamecommerce.model.Carrinho;
import com.flamecommerce.model.ItemCarrinho;
import com.flamecommerce.model.Produto;
import com.flamecommerce.model.Usuario;
import com.flamecommerce.repository.CarrinhoRepository;
import com.flamecommerce.repository.ProdutoRepository;
import com.flamecommerce.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ProdutoRepository produtoRepository,
                           UsuarioRepository usuarioRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Carrinho obter(String email) {
        return carrinhoRepository.findByUsuarioEmail(email).orElseGet(() -> {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RegraNegocioException("Faça login para usar o carrinho."));
            return carrinhoRepository.save(new Carrinho(usuario));
        });
    }

    @Transactional(readOnly = true)
    public int contarUnidades(String email) {
        Long total = carrinhoRepository.somarUnidades(email);
        return total == null ? 0 : total.intValue();
    }

    /** RN2: produto sem estoque (ou acima do estoque) não entra no carrinho. */
    public void adicionar(String email, Long produtoId, int quantidade) {
        if (quantidade < 1) {
            throw new RegraNegocioException("Informe uma quantidade válida.");
        }
        Carrinho carrinho = obter(email);
        Produto produto = produtoRepository.buscarComEstoque(produtoId)
                .filter(Produto::isAtivo)
                .orElseThrow(() -> new RegraNegocioException("Produto indisponível."));

        ItemCarrinho existente = carrinho.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElse(null);

        int novaQuantidade = (existente == null ? 0 : existente.getQuantidade()) + quantidade;
        garantirEstoque(produto, novaQuantidade);

        if (existente == null) {
            carrinho.adicionarItem(new ItemCarrinho(produto, quantidade));
        } else {
            existente.setQuantidade(novaQuantidade);
        }
    }

    public void alterarQuantidade(String email, Long itemId, int quantidade) {
        Carrinho carrinho = obter(email);
        ItemCarrinho item = localizarItem(carrinho, itemId);
        if (quantidade < 1) {
            carrinho.getItens().remove(item);
            return;
        }
        garantirEstoque(item.getProduto(), quantidade);
        item.setQuantidade(quantidade);
    }

    public void remover(String email, Long itemId) {
        Carrinho carrinho = obter(email);
        carrinho.getItens().remove(localizarItem(carrinho, itemId));
    }

    private ItemCarrinho localizarItem(Carrinho carrinho, Long itemId) {
        return carrinho.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException("Item não encontrado no carrinho."));
    }

    private void garantirEstoque(Produto produto, int quantidadeDesejada) {
        int disponivel = produto.getQuantidadeEstoque();
        if (disponivel == 0) {
            throw new RegraNegocioException(produto.getNome() + " está sem estoque.");
        }
        if (quantidadeDesejada > disponivel) {
            throw new RegraNegocioException("Só restam " + disponivel + " unidade(s) de " + produto.getNome() + ".");
        }
    }
}
