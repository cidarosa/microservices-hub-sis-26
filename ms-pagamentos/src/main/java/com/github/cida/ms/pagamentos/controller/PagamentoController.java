package com.github.cida.ms.pagamentos.controller;

import com.github.cida.ms.pagamentos.dto.PagamentoDTO;
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
    public ResponseEntity<List<PagamentoDTO>> getAll() {

        List<PagamentoDTO> list = pagamentoService.findAllPagamento();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> getOne(@PathVariable Long id) {

        PagamentoDTO pagamentoDTO = pagamentoService.findPagamentoById(id);

        return ResponseEntity.ok(pagamentoDTO);
    }

    @PostMapping
    public ResponseEntity<PagamentoDTO> save(@RequestBody @Valid PagamentoDTO pagamentoDTO) {

        pagamentoDTO = pagamentoService.save(pagamentoDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(pagamentoDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pagamentoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> update(@PathVariable Long id,
                                               @Valid @RequestBody PagamentoDTO pagamentoDTO) {

        pagamentoDTO = pagamentoService.update(id, pagamentoDTO);

        return ResponseEntity.ok(pagamentoDTO);
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido",
            fallbackMethod = "fallbackConfirmarPagamentoPendente")
    public ResponseEntity<PagamentoDTO> confirmarPagamentoDoPedido(@PathVariable
                                                                   @NotNull Long id) {

        PagamentoDTO dto = pagamentoService.confirmarPagamentoDoPedido(id);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<PagamentoDTO> fallbackConfirmarPagamentoPendente(Long id, Throwable e){

        log.error("Falha ao confirmar pedido {}. Ativando fallback. Erro: {}", id, e.getMessage());

        PagamentoDTO dto = pagamentoService.alterarStatusDoPagamento(id);

        return ResponseEntity.status(503).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        pagamentoService.deletePagamento(id);

        return ResponseEntity.noContent().build();
    }
}
