package com.github.cidarosa.ms.pedidos.controller;

import com.github.cidarosa.ms.pedidos.dto.PedidoDto;
import com.github.cidarosa.ms.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDto>> getAll() {
        List<PedidoDto> list = pedidoService.findAllPedidos();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> getPedido(@PathVariable Long id) {
        PedidoDto pedidoDto = pedidoService.findPedidoById(id);
        return ResponseEntity.ok(pedidoDto);
    }

    @PostMapping
    public ResponseEntity<PedidoDto> createPedido(@RequestBody @Valid PedidoDto pedidoDto) {
        pedidoDto = pedidoService.savePedido(pedidoDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedidoDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pedidoDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDto> updatePedido(@PathVariable Long id, @Valid @RequestBody PedidoDto pedidoDto) {
        pedidoDto = pedidoService.updatePedido(id, pedidoDto);
        return ResponseEntity.ok(pedidoDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoService.deletePedidoById(id);
        return ResponseEntity.noContent().build();
    }
}