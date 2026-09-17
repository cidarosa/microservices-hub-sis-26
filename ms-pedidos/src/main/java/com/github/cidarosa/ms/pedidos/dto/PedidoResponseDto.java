package com.github.cidarosa.ms.pedidos.dto;

import com.github.cidarosa.ms.pedidos.entities.ItemDoPedido;
import com.github.cidarosa.ms.pedidos.entities.Pedido;
import com.github.cidarosa.ms.pedidos.entities.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PedidoResponseDto {

    private Long id;

    private String nome;
    private String cpf;
    private LocalDate data;
    private Status status;

    private BigDecimal valorTotal;

    private List< ItemDoPedidoResponseDto> itens = new ArrayList<>();

    public PedidoResponseDto(Pedido pedido) {
        id = pedido.getId();
        nome = pedido.getNome();
        cpf = pedido.getCpf();
        data = pedido.getData();
        status = pedido.getStatus();
        valorTotal = pedido.getValorTotal();

        for (ItemDoPedido item : pedido.getItens()) {

            ItemDoPedidoResponseDto itemDTO = new ItemDoPedidoResponseDto(item);
            itens.add(itemDTO);
        }
    }
}
