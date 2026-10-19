package com.flamecommerce.controller;

import com.flamecommerce.dto.CheckoutForm;
import com.flamecommerce.exception.RegraNegocioException;
import com.flamecommerce.model.Carrinho;
import com.flamecommerce.model.MetodoPagamento;
import com.flamecommerce.model.Pedido;
import com.flamecommerce.service.CarrinhoService;
import com.flamecommerce.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final CarrinhoService carrinhoService;

    public PedidoController(PedidoService pedidoService, CarrinhoService carrinhoService) {
        this.pedidoService = pedidoService;
        this.carrinhoService = carrinhoService;
    }

    @GetMapping("/checkout")
    public String checkout(Principal principal, Model model, RedirectAttributes flash) {
        Carrinho carrinho = carrinhoService.obter(principal.getName());
        if (carrinho.isVazio()) {
            flash.addFlashAttribute("erro", "Seu carrinho está vazio.");
            return "redirect:/carrinho";
        }
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("form", new CheckoutForm());
        model.addAttribute("metodos", MetodoPagamento.values());
        return "pedidos/checkout";
    }

    @PostMapping("/checkout")
    public String finalizar(@Valid @ModelAttribute("form") CheckoutForm form,
                            BindingResult result,
                            Principal principal,
                            Model model,
                            RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                Pedido pedido = pedidoService.finalizarCompra(principal.getName(), form);
                flash.addFlashAttribute("sucesso", "Pedido #" + pedido.getId() + " confirmado! Obrigado pela compra.");
                return "redirect:/pedidos";
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        model.addAttribute("carrinho", carrinhoService.obter(principal.getName()));
        model.addAttribute("metodos", MetodoPagamento.values());
        return "pedidos/checkout";
    }

    @GetMapping("/pedidos")
    public String meusPedidos(Principal principal, Model model) {
        model.addAttribute("pedidos", pedidoService.listarDoCliente(principal.getName()));
        return "pedidos/lista";
    }

    @PostMapping("/pedidos/{id}/cancelar")
    public String cancelar(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        try {
            pedidoService.cancelarDoCliente(principal.getName(), id);
            flash.addFlashAttribute("sucesso", "Pedido #" + id + " cancelado e pagamento estornado.");
        } catch (RegraNegocioException e) {
            flash.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos";
    }
}
