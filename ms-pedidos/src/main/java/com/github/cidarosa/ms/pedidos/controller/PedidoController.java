package com.github.cidarosa.ms.pedidos.controller;

import com.github.cidarosa.ms.pedidos.dto.PedidoRequestDto;
import com.github.cidarosa.ms.pedidos.dto.PedidoResponseDto;
import com.github.cidarosa.ms.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

//    @GetMapping("/port")
//    public String port(@Value("${local.server.port}") String porta){
//        return "Instância respondeu na porta " + porta;
//    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDto>> getAll(){

        List<PedidoResponseDto> list = pedidoService.findAllPedidos();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDto> getPedido(@PathVariable Long id){

        PedidoResponseDto pedidoDto = pedidoService.findPedidoById(id);
        return ResponseEntity.ok(pedidoDto);
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDto> savePedido(@RequestBody @Valid
                                                        PedidoRequestDto requestDto){

        PedidoResponseDto  responseDto = pedidoService.savePedido(requestDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDto> updatePedido(@PathVariable Long id,
                                                          @Valid @RequestBody PedidoRequestDto requestDto){

        PedidoResponseDto responseDto = pedidoService.updatePedido(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{pedidoId}/pagamento/confirmado")
//    @Hidden
    public void confirmarPagamento(@PathVariable Long pedidoId){

        pedidoService.confirmarPagamento(pedidoId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id){

        pedidoService.deletePedidoById(id);

        return ResponseEntity.noContent().build();
    }
}
