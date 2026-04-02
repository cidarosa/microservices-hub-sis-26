package com.github.cidarosa.ms.pedidos.repositories;

import com.github.cidarosa.ms.pedidos.entities.ItemDoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDoPedidoRepository extends JpaRepository<ItemDoPedido, Long> {
}
