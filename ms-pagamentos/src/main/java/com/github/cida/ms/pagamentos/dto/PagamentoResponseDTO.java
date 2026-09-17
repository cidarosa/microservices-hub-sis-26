package com.github.cida.ms.pagamentos.dto;

import com.github.cida.ms.pagamentos.entities.Pagamento;
import com.github.cida.ms.pagamentos.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PagamentoResponseDTO {

    private Long id;
    private BigDecimal valor;
    private String nome;
//    private String numeroCartao;
//    private String validade;
//    private String codigoSeguranca;
    private Status status;
    private Long pedidoId;

    public PagamentoResponseDTO(Pagamento pagamento) {
        id = pagamento.getId();
        valor = pagamento.getValor();
        nome = pagamento.getNome();
//        numeroCartao = pagamento.getNumeroCartao();
//        validade = pagamento.getValidade();
//        codigoSeguranca = pagamento.getCodigoSeguranca();
        status = pagamento.getStatus();
        pedidoId = pagamento.getPedidoId();
    }
}
