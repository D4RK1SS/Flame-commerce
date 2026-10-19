package com.flamecommerce.dto;

import com.flamecommerce.model.MetodoPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CheckoutForm {

    @NotBlank(message = "Informe o CEP.")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido (ex.: 01001-000).")
    private String cep;

    @NotBlank(message = "Informe a rua.")
    @Size(max = 160)
    private String logradouro;

    @NotBlank(message = "Informe o número.")
    @Size(max = 20)
    private String numero;

    @Size(max = 80)
    private String complemento;

    @NotBlank(message = "Informe o bairro.")
    @Size(max = 80)
    private String bairro;

    @NotBlank(message = "Informe a cidade.")
    @Size(max = 80)
    private String cidade;

    @NotBlank(message = "Informe o estado.")
    @Pattern(regexp = "[A-Za-z]{2}", message = "Use a sigla (ex.: SP).")
    private String estado;

    @NotNull(message = "Escolha a forma de pagamento.")
    private MetodoPagamento metodo = MetodoPagamento.PIX;

    // Campos do cartão: só são exigidos (e validados no serviço) quando metodo = CARTAO.
    private String nomeCartao;
    private String numeroCartao;
    private String validade;
    private String cvv;

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public MetodoPagamento getMetodo() { return metodo; }
    public void setMetodo(MetodoPagamento metodo) { this.metodo = metodo; }
    public String getNomeCartao() { return nomeCartao; }
    public void setNomeCartao(String nomeCartao) { this.nomeCartao = nomeCartao; }
    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }
    public String getValidade() { return validade; }
    public void setValidade(String validade) { this.validade = validade; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}
