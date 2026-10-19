package com.flamecommerce.service;

import com.flamecommerce.model.MetodoPagamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Gateway de mentira para a atividade: Pix e boleto sempre aprovam;
 * cartão é recusado quando o número termina em 0000 (permite demonstrar o fluxo de recusa).
 */
@Component
public class PagamentoGatewaySimulado implements PagamentoGateway {

    @Override
    public boolean autorizar(MetodoPagamento metodo, String numeroCartao, BigDecimal valor) {
        if (metodo != MetodoPagamento.CARTAO) {
            return true;
        }
        String digitos = numeroCartao == null ? "" : numeroCartao.replaceAll("\\D", "");
        return !digitos.endsWith("0000");
    }
}
