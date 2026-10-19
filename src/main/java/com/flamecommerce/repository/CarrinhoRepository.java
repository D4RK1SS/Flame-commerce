package com.flamecommerce.repository;

import com.flamecommerce.model.Carrinho;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    @EntityGraph(attributePaths = {"itens", "itens.produto"})
    Optional<Carrinho> findByUsuarioEmail(String email);

    /** Soma das unidades no carrinho (null quando vazio). */
    @Query("select sum(i.quantidade) from ItemCarrinho i where i.carrinho.usuario.email = :email")
    Long somarUnidades(@Param("email") String email);
}
