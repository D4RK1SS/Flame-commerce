package com.flamecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrinhos")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinho> itens = new ArrayList<>();

    protected Carrinho() {
    }

    public Carrinho(Usuario usuario) {
        this.usuario = usuario;
    }

    public void adicionarItem(ItemCarrinho item) {
        item.setCarrinho(this);
        itens.add(item);
    }

    public void limpar() {
        itens.clear();
    }

    public BigDecimal getTotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getQuantidadeTotal() {
        return itens.stream().mapToInt(ItemCarrinho::getQuantidade).sum();
    }

    public boolean isVazio() {
        return itens.isEmpty();
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public List<ItemCarrinho> getItens() { return itens; }
}
