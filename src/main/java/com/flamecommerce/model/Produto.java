package com.flamecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false, length = 60)
    private String categoria;

    @Column(length = 16)
    private String emoji;

    @Column(name = "imagem_url", length = 500)
    private String imagemUrl;

    /** Produto inativo some da vitrine, mas continua no histórico dos pedidos. */
    @Column(nullable = false)
    private boolean ativo = true;

    @OneToOne(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Estoque estoque;

    public void definirEstoque(int quantidade) {
        if (estoque == null) {
            estoque = new Estoque(this, quantidade);
        } else {
            estoque.setQuantidade(quantidade);
        }
    }

    public int getQuantidadeEstoque() {
        return estoque == null ? 0 : estoque.getQuantidade();
    }

    /** RN2: só pode ser vendido se estiver ativo e com estoque. */
    public boolean isDisponivel() {
        return ativo && getQuantidadeEstoque() > 0;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public Estoque getEstoque() { return estoque; }
}
