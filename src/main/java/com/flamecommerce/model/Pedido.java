package com.flamecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status = StatusPedido.CONFIRMADO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Entrega entrega;

    protected Pedido() {
    }

    public Pedido(Usuario usuario) {
        this.usuario = usuario;
    }

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
    }

    public void recalcularTotal() {
        total = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** RN5: o cancelamento só é permitido antes do envio. */
    public boolean isCancelavel() {
        return status == StatusPedido.CONFIRMADO;
    }

    public boolean isPodeAvancar() {
        return status == StatusPedido.CONFIRMADO || status == StatusPedido.ENVIADO;
    }

    public void avancar() {
        if (status == StatusPedido.CONFIRMADO) {
            status = StatusPedido.ENVIADO;
        } else if (status == StatusPedido.ENVIADO) {
            status = StatusPedido.ENTREGUE;
        }
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public BigDecimal getTotal() { return total; }
    public List<ItemPedido> getItens() { return itens; }
    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }
    public Entrega getEntrega() { return entrega; }
    public void setEntrega(Entrega entrega) { this.entrega = entrega; }
}
