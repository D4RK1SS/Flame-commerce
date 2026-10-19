package com.flamecommerce.model;

public enum StatusPagamento {
    APROVADO("Aprovado"),
    ESTORNADO("Estornado");

    private final String rotulo;

    StatusPagamento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
