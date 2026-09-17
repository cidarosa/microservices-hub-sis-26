package com.github.cidarosa.ms.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PedidoRequestDto {

    @NotBlank(message = "Nome é requerido")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é requerido")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres")
    private String cpf;

//    private LocalDate data;
//    private Status status;

//    private BigDecimal valorTotal;

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<@Valid ItemDoPedidoRequestDto> itens = new ArrayList<>();


}
