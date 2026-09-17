package com.github.cidarosa.ms.pedidos.dto;

import com.github.cidarosa.ms.pedidos.entities.ItemDoPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ItemDoPedidoRequestDto {

    @NotNull(message = "Quantidade é requerida")
    @Positive(message = "Quantidade deve ser um número positivo")
    private Integer quantidade;

    @NotBlank(message = "Descrição requerida")
    private String descricao;

    @NotNull(message = "Preço unitário é requerido")
    @Positive(message = "Preço unitário dever ser um valor positivo e maior que zero")
    private BigDecimal precoUnitario;

}
