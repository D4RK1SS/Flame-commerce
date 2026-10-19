package com.flamecommerce.repository;

import com.flamecommerce.model.Pedido;
import com.flamecommerce.model.StatusPedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"itens", "pagamento", "entrega"})
    List<Pedido> findByUsuarioEmailOrderByCriadoEmDesc(String email);

    @EntityGraph(attributePaths = {"itens", "pagamento", "entrega"})
    List<Pedido> findAllByOrderByCriadoEmDesc();

    @EntityGraph(attributePaths = {"itens", "pagamento", "entrega"})
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> buscarDetalhado(@Param("id") Long id);

    /** Receita = pedidos que não foram cancelados (null quando não há pedidos). */
    @Query("select sum(p.total) from Pedido p where p.status <> :excluido")
    BigDecimal somarReceitaExceto(@Param("excluido") StatusPedido excluido);

    long countByStatus(StatusPedido status);
}
