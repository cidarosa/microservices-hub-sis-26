package com.github.cida.ms.pagamentos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PagamentoRequestDTO {

    @NotNull(message = "O campo valor é obrigatório")
    @Positive(message = "O campo valor deve ser um número positivo")
    private BigDecimal valor;

    @NotBlank(message = "O campo nome é obrigatório")
    @Size(min = 3, max = 50, message = "O campo nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotBlank(message = "O campo número do cartão é obrigatório")
    @Size(min = 16, max = 16, message = "O campo número do cartão deve ter 16 caracteres")
    private String numeroCartao;

    @NotBlank(message = "O campo validade é obrigatório")
    @Size(min = 5, max = 5, message = "O campo validade deve ter 5 caracteres")
    private String validade;

    @NotBlank(message = "O campo código de segurança é obrigatório")
    @Size(min = 3, max = 3, message = "O campo código de segurança deve ter 3 caracteres")
    private String codigoSeguranca;

    @NotNull(message = "O campo pedido id é obrigatório")
    private Long pedidoId;


}
