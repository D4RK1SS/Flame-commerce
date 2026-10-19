package com.flamecommerce.dto;

import com.flamecommerce.model.Produto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProdutoForm {

    private Long id;

    @NotBlank(message = "Informe o nome do produto.")
    @Size(max = 140, message = "Máximo de 140 caracteres.")
    private String nome;

    @Size(max = 1000, message = "Máximo de 1000 caracteres.")
    private String descricao;

    @NotNull(message = "Informe o preço.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal preco;

    @NotBlank(message = "Informe a categoria.")
    @Size(max = 60, message = "Máximo de 60 caracteres.")
    private String categoria;

    @Size(max = 16, message = "Use apenas um emoji.")
    private String emoji;

    @Size(max = 500, message = "URL muito longa.")
    private String imagemUrl;

    @NotNull(message = "Informe a quantidade em estoque.")
    @Min(value = 0, message = "O estoque não pode ser negativo.")
    private Integer quantidadeEstoque = 0;

    private boolean ativo = true;

    public static ProdutoForm de(Produto p) {
        ProdutoForm f = new ProdutoForm();
        f.id = p.getId();
        f.nome = p.getNome();
        f.descricao = p.getDescricao();
        f.preco = p.getPreco();
        f.categoria = p.getCategoria();
        f.emoji = p.getEmoji();
        f.imagemUrl = p.getImagemUrl();
        f.quantidadeEstoque = p.getQuantidadeEstoque();
        f.ativo = p.isAtivo();
        return f;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
