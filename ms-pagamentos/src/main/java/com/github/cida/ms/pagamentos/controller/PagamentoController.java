package com.github.cida.ms.pagamentos.controller;

import com.github.cida.ms.pagamentos.dto.PagamentoRequestDTO;
import com.github.cida.ms.pagamentos.dto.PagamentoResponseDTO;
import com.github.cida.ms.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<List<PagamentoResponseDTO>> getAll() {

        List<PagamentoResponseDTO> list = pagamentoService.findAllPagamento();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> getOne(@PathVariable Long id) {

        PagamentoResponseDTO pagamentoDTO = pagamentoService.findPagamentoById(id);

        return ResponseEntity.ok(pagamentoDTO);
    }

    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> save(@RequestBody @Valid PagamentoRequestDTO requestDTO) {

        PagamentoResponseDTO responseDTO = pagamentoService.save(requestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> update(@PathVariable Long id,
                                                       @Valid @RequestBody PagamentoRequestDTO requestDTO) {

        PagamentoResponseDTO responseDTO = pagamentoService.update(id, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido",
            fallbackMethod = "fallbackConfirmarPagamentoPendente")
    public ResponseEntity<PagamentoResponseDTO> confirmarPagamentoDoPedido(@PathVariable
                                                                   @NotNull Long id) {

        PagamentoResponseDTO dto = pagamentoService.confirmarPagamentoDoPedido(id);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<PagamentoResponseDTO> fallbackConfirmarPagamentoPendente(Long id, Throwable e){

        log.error("Falha ao confirmar pedido {}. Ativando fallback. Erro: {}", id, e.getMessage());

        PagamentoResponseDTO dto = pagamentoService.alterarStatusDoPagamento(id);

        return ResponseEntity.status(503).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        pagamentoService.deletePagamento(id);

        return ResponseEntity.noContent().build();
    }
}
