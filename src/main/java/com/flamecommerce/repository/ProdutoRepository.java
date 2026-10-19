package com.flamecommerce.repository;

import com.flamecommerce.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /** Vitrine: só produtos ativos. Parâmetros vazios ("") significam "sem filtro". */
    @Query("""
            select p from Produto p left join fetch p.estoque
            where p.ativo = true
              and lower(p.nome) like lower(concat('%', :busca, '%'))
              and (:categoria = '' or p.categoria = :categoria)
            order by p.nome
            """)
    List<Produto> buscarVitrine(@Param("busca") String busca, @Param("categoria") String categoria);

    @Query("select distinct p.categoria from Produto p where p.ativo = true order by p.categoria")
    List<String> listarCategoriasAtivas();

    @Query("select p from Produto p left join fetch p.estoque order by p.nome")
    List<Produto> listarTodosComEstoque();

    @Query("select p from Produto p left join fetch p.estoque where p.id = :id")
    Optional<Produto> buscarComEstoque(@Param("id") Long id);

    long countByAtivoTrue();

    @Query("select count(e) from Estoque e where e.quantidade = 0")
    long contarSemEstoque();
}
