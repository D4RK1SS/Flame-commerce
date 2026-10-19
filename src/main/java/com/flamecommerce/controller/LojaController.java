package com.flamecommerce.controller;

import com.flamecommerce.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LojaController {

    private final ProdutoService produtoService;

    public LojaController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/")
    public String vitrine(@RequestParam(name = "q", defaultValue = "") String busca,
                          @RequestParam(name = "categoria", defaultValue = "") String categoria,
                          Model model) {
        model.addAttribute("produtos", produtoService.listarVitrine(busca, categoria));
        model.addAttribute("categorias", produtoService.listarCategorias());
        model.addAttribute("busca", busca);
        model.addAttribute("categoriaAtiva", categoria);
        return "loja/index";
    }

    @GetMapping("/produtos/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("produto", produtoService.buscar(id));
        return "loja/produto";
    }
}
