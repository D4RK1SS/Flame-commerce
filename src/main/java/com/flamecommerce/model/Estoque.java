package com.flamecommerce.model;

import com.flamecommerce.exception.RegraNegocioException;
import jakarta.persistence.*;

@Entity
@Table(name = "estoques")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    @Column(nullable = false)
    private int quantidade;

    /** Controle de concorrência otimista: evita vender a mesma unidade duas vezes. */
    @Version
    private Long versao;

    protected Estoque() {
    }

    public Estoque(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public void baixar(int unidades) {
        if (unidades > quantidade) {
            throw new RegraNegocioException("Estoque insuficiente para " + produto.getNome() + ".");
        }
        quantidade -= unidades;
    }

    public void repor(int unidades) {
        quantidade += unidades;
    }

    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
