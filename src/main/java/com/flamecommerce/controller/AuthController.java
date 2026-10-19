package com.flamecommerce.controller;

import com.flamecommerce.dto.CadastroForm;
import com.flamecommerce.exception.RegraNegocioException;
import com.flamecommerce.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        return principal != null ? "redirect:/" : "auth/login";
    }

    @GetMapping("/cadastro")
    public String formularioCadastro(Principal principal, Model model) {
        if (principal != null) {
            return "redirect:/";
        }
        model.addAttribute("form", new CadastroForm());
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("form") CadastroForm form, BindingResult result) {
        if (form.getSenha() != null && !form.getSenha().equals(form.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "senhas.diferentes", "As senhas não conferem.");
        }
        if (result.hasErrors()) {
            return "auth/cadastro";
        }
        try {
            usuarioService.cadastrar(form);
        } catch (RegraNegocioException e) {
            result.rejectValue("email", "email.duplicado", e.getMessage());
            return "auth/cadastro";
        }
        return "redirect:/login?cadastrado";
    }
}
