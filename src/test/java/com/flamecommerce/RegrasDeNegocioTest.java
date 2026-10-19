package com.flamecommerce;

import com.flamecommerce.dto.CadastroForm;
import com.flamecommerce.dto.CheckoutForm;
import com.flamecommerce.dto.ProdutoForm;
import com.flamecommerce.exception.RegraNegocioException;
import com.flamecommerce.model.MetodoPagamento;
import com.flamecommerce.model.Pedido;
import com.flamecommerce.model.Produto;
import com.flamecommerce.model.StatusPedido;
import com.flamecommerce.service.CarrinhoService;
import com.flamecommerce.service.PedidoService;
import com.flamecommerce.service.ProdutoService;
import com.flamecommerce.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes das regras RN1–RN5 do quadro do Trello (cada teste é desfeito ao final). */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:flame-test;DB_CLOSE_DELAY=-1")
@Transactional
class RegrasDeNegocioTest {

    @Autowired private UsuarioService usuarioService;
    @Autowired private ProdutoService produtoService;
    @Autowired private CarrinhoService carrinhoService;
    @Autowired private PedidoService pedidoService;

    private String novoCliente() {
        String email = UUID.randomUUID() + "@teste.com";
        CadastroForm form = new CadastroForm();
        form.setNome("Cliente Teste");
        form.setEmail(email);
        form.setSenha("123456");
        form.setConfirmarSenha("123456");
        usuarioService.cadastrar(form);
        return email;
    }

    private Produto novoProduto(int estoque) {
        ProdutoForm form = new ProdutoForm();
        form.setNome("Produto " + UUID.randomUUID());
        form.setPreco(new BigDecimal("100.00"));
        form.setCategoria("Teste");
        form.setQuantidadeEstoque(estoque);
        return produtoService.salvar(form);
    }

    private CheckoutForm checkout(MetodoPagamento metodo, String numeroCartao) {
        CheckoutForm form = new CheckoutForm();
        form.setCep("01001-000");
        form.setLogradouro("Praça da Sé");
        form.setNumero("100");
        form.setBairro("Sé");
        form.setCidade("São Paulo");
        form.setEstado("SP");
        form.setMetodo(metodo);
        form.setNumeroCartao(numeroCartao);
        form.setNomeCartao("CLIENTE TESTE");
        form.setValidade("12/30");
        form.setCvv("123");
        return form;
    }

    @Test
    void rn2_produtoSemEstoqueNaoEntraNoCarrinho() {
        String email = novoCliente();
        Produto esgotado = novoProduto(0);

        assertThrows(RegraNegocioException.class,
                () -> carrinhoService.adicionar(email, esgotado.getId(), 1));
    }

    @Test
    void rn2_naoPermiteMaisUnidadesQueOEstoque() {
        String email = novoCliente();
        Produto produto = novoProduto(2);

        assertThrows(RegraNegocioException.class,
                () -> carrinhoService.adicionar(email, produto.getId(), 3));
    }

    @Test
    void rn3_pagamentoRecusadoNaoCriaPedidoNemBaixaEstoque() {
        String email = novoCliente();
        Produto produto = novoProduto(5);
        carrinhoService.adicionar(email, produto.getId(), 1);

        assertThrows(RegraNegocioException.class,
                () -> pedidoService.finalizarCompra(email, checkout(MetodoPagamento.CARTAO, "4111 1111 1111 0000")));

        assertTrue(pedidoService.listarDoCliente(email).isEmpty());
        assertEquals(5, produtoService.buscar(produto.getId()).getQuantidadeEstoque());
    }

    @Test
    void rn3_rn4_compraAprovadaConfirmaPedidoBaixaEstoqueEGuardaPreco() {
        String email = novoCliente();
        Produto produto = novoProduto(5);
        carrinhoService.adicionar(email, produto.getId(), 2);

        Pedido pedido = pedidoService.finalizarCompra(email, checkout(MetodoPagamento.PIX, null));

        assertEquals(StatusPedido.CONFIRMADO, pedido.getStatus());
        assertEquals(0, new BigDecimal("200.00").compareTo(pedido.getTotal()));
        assertEquals(3, produtoService.buscar(produto.getId()).getQuantidadeEstoque());
        assertTrue(carrinhoService.obter(email).isVazio());
    }

    @Test
    void rn5_cancelamentoRepoeEstoqueSoAntesDoEnvio() {
        String email = novoCliente();
        Produto produto = novoProduto(5);
        carrinhoService.adicionar(email, produto.getId(), 2);
        Pedido pedido = pedidoService.finalizarCompra(email, checkout(MetodoPagamento.PIX, null));

        pedidoService.cancelarDoCliente(email, pedido.getId());
        assertEquals(5, produtoService.buscar(produto.getId()).getQuantidadeEstoque());

        // novo pedido que já foi enviado não pode mais ser cancelado
        carrinhoService.adicionar(email, produto.getId(), 1);
        Pedido enviado = pedidoService.finalizarCompra(email, checkout(MetodoPagamento.PIX, null));
        pedidoService.avancarStatus(enviado.getId());

        assertThrows(RegraNegocioException.class,
                () -> pedidoService.cancelarDoCliente(email, enviado.getId()));
    }
}
