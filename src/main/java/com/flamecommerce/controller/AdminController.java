package com.flamecommerce.controller;

import com.flamecommerce.dto.ProdutoForm;
import com.flamecommerce.exception.RegraNegocioException;
import com.flamecommerce.model.Pedido;
import com.flamecommerce.model.StatusPedido;
import com.flamecommerce.service.PedidoService;
import com.flamecommerce.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/** Painel do vendedor/administrador (acesso restrito a ROLE_ADMIN no SecurityConfig). */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public AdminController(ProdutoService produtoService, PedidoService pedidoService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String painel(Model model) {
        List<Pedido> pedidos = pedidoService.listarTodos();
        model.addAttribute("receita", pedidoService.receitaTotal());
        model.addAttribute("totalPedidos", pedidoService.contarPedidos());
        model.addAttribute("pedidosAbertos", pedidoService.contarPorStatus(StatusPedido.CONFIRMADO));
        model.addAttribute("produtosAtivos", produtoService.contarAtivos());
        model.addAttribute("semEstoque", produtoService.contarSemEstoque());
        model.addAttribute("recentes", pedidos.size() > 5 ? pedidos.subList(0, 5) : pedidos);
        return "admin/dashboard";
    }

    // ---------- Produtos e estoque ----------

    @GetMapping("/produtos")
    public String produtos(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        return "admin/produtos";
    }

    @GetMapping("/produtos/novo")
    public String novoProduto(Model model) {
        model.addAttribute("form", new ProdutoForm());
        return "admin/produto-form";
    }

    @GetMapping("/produtos/{id}/editar")
    public String editarProduto(@PathVariable Long id, Model model) {
        model.addAttribute("form", ProdutoForm.de(produtoService.buscar(id)));
        return "admin/produto-form";
    }

    @PostMapping("/produtos")
    public String salvarProduto(@Valid @ModelAttribute("form") ProdutoForm form,
                                BindingResult result,
                                RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "admin/produto-form";
        }
        produtoService.salvar(form);
        flash.addFlashAttribute("sucesso", "Produto salvo com sucesso.");
        return "redirect:/admin/produtos";
    }

    @PostMapping("/produtos/{id}/ativo")
    public String alternarAtivo(@PathVariable Long id, RedirectAttributes flash) {
        produtoService.alternarAtivo(id);
        flash.addFlashAttribute("sucesso", "Status do produto atualizado.");
        return "redirect:/admin/produtos";
    }

    // ---------- Pedidos ----------

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "admin/pedidos";
    }

    @PostMapping("/pedidos/{id}/avancar")
    public String avancar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            pedidoService.avancarStatus(id);
            flash.addFlashAttribute("sucesso", "Pedido #" + id + " atualizado.");
        } catch (RegraNegocioException e) {
            flash.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/pedidos/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            pedidoService.cancelarComoAdmin(id);
            flash.addFlashAttribute("sucesso", "Pedido #" + id + " cancelado.");
        } catch (RegraNegocioException e) {
            flash.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }
}
