package com.flamecommerce.model;

/** Ciclo de vida do pedido. Só nasce CONFIRMADO porque o pedido exige pagamento aprovado (RN3). */
public enum StatusPedido {
    CONFIRMADO("Confirmado"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String rotulo;

    StatusPedido(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
