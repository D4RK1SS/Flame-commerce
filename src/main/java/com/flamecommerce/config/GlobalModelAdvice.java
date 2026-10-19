package com.flamecommerce.config;

import com.flamecommerce.service.CarrinhoService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/** Disponibiliza o contador do carrinho (badge do cabeçalho) em todas as telas. */
@ControllerAdvice
public class GlobalModelAdvice {

    private final CarrinhoService carrinhoService;

    public GlobalModelAdvice(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @ModelAttribute("cartCount")
    public int cartCount(Principal principal) {
        return principal == null ? 0 : carrinhoService.contarUnidades(principal.getName());
    }
}
