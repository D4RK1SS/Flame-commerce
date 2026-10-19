package com.flamecommerce.service;

import com.flamecommerce.model.MetodoPagamento;

import java.math.BigDecimal;

/** Porta de saída para o gateway de pagamento (no projeto real seria Mercado Pago, Stripe etc.). */
public interface PagamentoGateway {

    boolean autorizar(MetodoPagamento metodo, String numeroCartao, BigDecimal valor);
}
