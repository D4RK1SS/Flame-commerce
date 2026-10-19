package com.flamecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamento status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    protected Pagamento() {
    }

    public Pagamento(Pedido pedido, MetodoPagamento metodo, StatusPagamento status, BigDecimal valor) {
        this.pedido = pedido;
        this.metodo = metodo;
        this.status = status;
        this.valor = valor;
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public MetodoPagamento getMetodo() { return metodo; }
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
