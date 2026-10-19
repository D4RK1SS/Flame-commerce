package com.flamecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "entregas")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(nullable = false, length = 160)
    private String logradouro;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(length = 80)
    private String complemento;

    @Column(nullable = false, length = 80)
    private String bairro;

    @Column(nullable = false, length = 80)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    protected Entrega() {
    }

    public Entrega(Pedido pedido, String cep, String logradouro, String numero, String complemento,
                   String bairro, String cidade, String estado) {
        this.pedido = pedido;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    public String getEnderecoResumido() {
        String comp = (complemento == null || complemento.isBlank()) ? "" : " - " + complemento;
        return logradouro + ", " + numero + comp + " · " + bairro + " · " + cidade + "/" + estado + " · CEP " + cep;
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
}
