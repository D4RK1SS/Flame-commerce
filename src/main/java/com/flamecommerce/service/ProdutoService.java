package com.flamecommerce.service;

import com.flamecommerce.dto.ProdutoForm;
import com.flamecommerce.exception.RecursoNaoEncontradoException;
import com.flamecommerce.model.Produto;
import com.flamecommerce.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<Produto> listarVitrine(String busca, String categoria) {
        return produtoRepository.buscarVitrine(normalizar(busca), normalizar(categoria));
    }

    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        return produtoRepository.listarCategoriasAtivas();
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.listarTodosComEstoque();
    }

    @Transactional(readOnly = true)
    public Produto buscar(Long id) {
        return produtoRepository.buscarComEstoque(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
    }

    /** Vendedor/admin: cria ou atualiza o produto e o estoque (história de usuário 7). */
    @Transactional
    public Produto salvar(ProdutoForm form) {
        Produto produto = form.getId() == null ? new Produto() : buscar(form.getId());
        produto.setNome(form.getNome().trim());
        produto.setDescricao(vazioParaNulo(form.getDescricao()));
        produto.setPreco(form.getPreco());
        produto.setCategoria(form.getCategoria().trim());
        produto.setEmoji(vazioParaNulo(form.getEmoji()));
        produto.setImagemUrl(vazioParaNulo(form.getImagemUrl()));
        produto.setAtivo(form.isAtivo());
        produto.definirEstoque(form.getQuantidadeEstoque());
        return produtoRepository.save(produto);
    }

    @Transactional
    public void alternarAtivo(Long id) {
        Produto produto = buscar(id);
        produto.setAtivo(!produto.isAtivo());
    }

    @Transactional(readOnly = true)
    public long contarAtivos() {
        return produtoRepository.countByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public long contarSemEstoque() {
        return produtoRepository.contarSemEstoque();
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    private String vazioParaNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
