package com.flamecommerce.controller;

import com.flamecommerce.exception.RegraNegocioException;
import com.flamecommerce.service.CarrinhoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.security.Principal;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @GetMapping
    public String ver(Principal principal, Model model) {
        model.addAttribute("carrinho", carrinhoService.obter(principal.getName()));
        return "carrinho/index";
    }

    @PostMapping("/adicionar")
    public String adicionar(@RequestParam Long produtoId,
                            @RequestParam(defaultValue = "1") int quantidade,
                            @RequestHeader(value = "Referer", required = false) String referer,
                            Principal principal,
                            RedirectAttributes flash) {
        try {
            carrinhoService.adicionar(principal.getName(), produtoId, quantidade);
            flash.addFlashAttribute("sucesso", "Produto adicionado ao carrinho.");
        } catch (RegraNegocioException e) {
            flash.addFlashAttribute("erro", e.getMessage());
        }
        return voltarPara(referer);
    }

    @PostMapping("/item/{itemId}/alterar")
    public String alterar(@PathVariable Long itemId,
                          @RequestParam int quantidade,
                          Principal principal,
                          RedirectAttributes flash) {
        try {
            carrinhoService.alterarQuantidade(principal.getName(), itemId, quantidade);
        } catch (RegraNegocioException e) {
            flash.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/item/{itemId}/remover")
    public String remover(@PathVariable Long itemId, Principal principal, RedirectAttributes flash) {
        try {
            carrinhoService.remover(principal.getName(), itemId);
            flash.addFlashAttribute("sucesso", "Item removido do carrinho.");
        } catch (RegraNegocioException e) {
            flash.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/carrinho";
    }

    /** Volta para a página de onde o usuário veio, usando só path+query (evita redirecionar para outro site). */
    private String voltarPara(String referer) {
        if (referer == null) {
            return "redirect:/carrinho";
        }
        try {
            URI uri = URI.create(referer);
            String path = uri.getRawPath();
            if (path == null || !path.startsWith("/") || path.startsWith("//")) {
                return "redirect:/carrinho";
            }
            return "redirect:" + path + (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");
        } catch (IllegalArgumentException e) {
            return "redirect:/carrinho";
        }
    }
}
