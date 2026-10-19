package com.flamecommerce.model;

public enum MetodoPagamento {
    PIX("Pix"),
    CARTAO("Cartão de crédito"),
    BOLETO("Boleto");

    private final String rotulo;

    MetodoPagamento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
