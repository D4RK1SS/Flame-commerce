package com.flamecommerce.service;

import com.flamecommerce.dto.CheckoutForm;
import com.flamecommerce.exception.RecursoNaoEncontradoException;
import com.flamecommerce.exception.RegraNegocioException;
import com.flamecommerce.model.*;
import com.flamecommerce.repository.CarrinhoRepository;
import com.flamecommerce.repository.PedidoRepository;
import com.flamecommerce.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagamentoGateway pagamentoGateway;

    public PedidoService(PedidoRepository pedidoRepository,
                         CarrinhoRepository carrinhoRepository,
                         UsuarioRepository usuarioRepository,
                         PagamentoGateway pagamentoGateway) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pagamentoGateway = pagamentoGateway;
    }

    /**
     * Fluxo do diagrama de sequência: verificarCadastro (RN1) → verificarEstoque (RN2) →
     * processarPagamento → [aprovado] confirmarPedido (RN3) / [recusado] pede outra forma de pagamento.
     * Se o pagamento for recusado nada é gravado: o pedido só existe após aprovação.
     */
    public Pedido finalizarCompra(String email, CheckoutForm form) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraNegocioException("Faça login para finalizar a compra.")); // RN1

        Carrinho carrinho = carrinhoRepository.findByUsuarioEmail(email)
                .filter(c -> !c.isVazio())
                .orElseThrow(() -> new RegraNegocioException("Seu carrinho está vazio."));

        for (ItemCarrinho item : carrinho.getItens()) { // RN2
            Produto produto = item.getProduto();
            if (!produto.isAtivo() || produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new RegraNegocioException(
                        "Estoque insuficiente para " + produto.getNome() + ". Ajuste o carrinho e tente novamente.");
            }
        }

        validarCartao(form);

        Pedido pedido = new Pedido(usuario);
        for (ItemCarrinho item : carrinho.getItens()) { // RN4: congela o preço vigente
            Produto produto = item.getProduto();
            pedido.adicionarItem(new ItemPedido(produto, item.getQuantidade(), produto.getPreco()));
        }
        pedido.recalcularTotal();

        boolean aprovado = pagamentoGateway.autorizar(form.getMetodo(), form.getNumeroCartao(), pedido.getTotal());
        if (!aprovado) { // RN3
            throw new RegraNegocioException("Pagamento recusado. Escolha outra forma de pagamento.");
        }

        pedido.setPagamento(new Pagamento(pedido, form.getMetodo(), StatusPagamento.APROVADO, pedido.getTotal()));
        pedido.setEntrega(new Entrega(pedido, form.getCep(), form.getLogradouro().trim(), form.getNumero().trim(),
                form.getComplemento(), form.getBairro().trim(), form.getCidade().trim(),
                form.getEstado().trim().toUpperCase()));
        pedido.setStatus(StatusPedido.CONFIRMADO);

        for (ItemPedido item : pedido.getItens()) {
            item.getProduto().getEstoque().baixar(item.getQuantidade());
        }
        carrinho.limpar();
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarDoCliente(String email) {
        return pedidoRepository.findByUsuarioEmailOrderByCriadoEmDesc(email);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByCriadoEmDesc();
    }

    /** RN5: cancelamento só antes do envio. Repõe o estoque e estorna o pagamento. */
    public void cancelarDoCliente(String email, Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        if (!pedido.getUsuario().getEmail().equals(email)) {
            throw new RecursoNaoEncontradoException("Pedido não encontrado.");
        }
        cancelar(pedido);
    }

    public void cancelarComoAdmin(Long pedidoId) {
        cancelar(buscar(pedidoId));
    }

    public void avancarStatus(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        if (!pedido.isPodeAvancar()) {
            throw new RegraNegocioException("Este pedido não pode mais avançar de status.");
        }
        pedido.avancar();
    }

    @Transactional(readOnly = true)
    public long contarPedidos() {
        return pedidoRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarPorStatus(StatusPedido status) {
        return pedidoRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public BigDecimal receitaTotal() {
        BigDecimal receita = pedidoRepository.somarReceitaExceto(StatusPedido.CANCELADO);
        return receita == null ? BigDecimal.ZERO : receita;
    }

    private Pedido buscar(Long id) {
        return pedidoRepository.buscarDetalhado(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
    }

    private void cancelar(Pedido pedido) {
        if (!pedido.isCancelavel()) {
            throw new RegraNegocioException("Cancelamento só é permitido antes do envio do pedido.");
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.getPagamento().setStatus(StatusPagamento.ESTORNADO);
        for (ItemPedido item : pedido.getItens()) {
            item.getProduto().getEstoque().repor(item.getQuantidade());
        }
    }

    private void validarCartao(CheckoutForm form) {
        if (form.getMetodo() != MetodoPagamento.CARTAO) {
            return;
        }
        String numero = form.getNumeroCartao() == null ? "" : form.getNumeroCartao().replaceAll("\\D", "");
        if (numero.length() < 13 || numero.length() > 19) {
            throw new RegraNegocioException("Número do cartão inválido.");
        }
        if (form.getNomeCartao() == null || form.getNomeCartao().isBlank()) {
            throw new RegraNegocioException("Informe o nome impresso no cartão.");
        }
        if (form.getValidade() == null || !form.getValidade().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new RegraNegocioException("Validade inválida (use MM/AA).");
        }
        if (form.getCvv() == null || !form.getCvv().matches("\\d{3,4}")) {
            throw new RegraNegocioException("CVV inválido.");
        }
    }
}
