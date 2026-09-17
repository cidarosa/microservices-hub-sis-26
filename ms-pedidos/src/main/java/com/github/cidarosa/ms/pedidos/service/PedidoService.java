package com.github.cidarosa.ms.pedidos.service;

import com.github.cidarosa.ms.pedidos.dto.ItemDoPedidoRequestDto;
import com.github.cidarosa.ms.pedidos.dto.ItemDoPedidoResponseDto;
import com.github.cidarosa.ms.pedidos.dto.PedidoRequestDto;
import com.github.cidarosa.ms.pedidos.dto.PedidoResponseDto;
import com.github.cidarosa.ms.pedidos.entities.ItemDoPedido;
import com.github.cidarosa.ms.pedidos.entities.Pedido;
import com.github.cidarosa.ms.pedidos.entities.Status;
import com.github.cidarosa.ms.pedidos.exceptions.PedidoPagoException;
import com.github.cidarosa.ms.pedidos.exceptions.ResourceNotFoundException;
import com.github.cidarosa.ms.pedidos.repositories.ItemDoPedidoRepository;
import com.github.cidarosa.ms.pedidos.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

//    @Autowired
//    private ItemDoPedidoRepository itemDoPedidoRepository;

    @Transactional(readOnly = true)
    public List<PedidoResponseDto> findAllPedidos(){

        return pedidoRepository.findAll()
                .stream().map(PedidoResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponseDto findPedidoById(Long id){

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );
        return new PedidoResponseDto(pedido);
    }

    @Transactional
    public PedidoResponseDto savePedido(PedidoRequestDto requestDto){

        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(requestDto, pedido);
        pedido.calcularValorTotalDoPedido();
        pedido = pedidoRepository.save(pedido);
        return new PedidoResponseDto(pedido);
    }

    @Transactional
    public PedidoResponseDto updatePedido(Long id, PedidoRequestDto requestDto){

        try {
            Pedido pedido = pedidoRepository.getReferenceById(id);
            if (pedido.getStatus().equals(Status.PAGO)){
                throw new PedidoPagoException(
                        String.format("Pedido id: %d já está PAGO e não pode ser alterado.", id)
                );
            }
            pedido.getItens().clear();
            pedido.setData(LocalDate.now());
            pedido.setStatus(Status.CRIADO);
            mapDtoToPedido(requestDto, pedido);
            pedido.calcularValorTotalDoPedido();
            pedido = pedidoRepository.save(pedido);
            return new PedidoResponseDto(pedido);
        } catch (EntityNotFoundException e) {
            throw new  ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    public void deletePedidoById(Long id){

        if(!pedidoRepository.existsById(id)){
            throw new  ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        pedidoRepository.deleteById(id);
    }

    @Transactional
    public void confirmarPagamento(Long id){

        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if (pedido.isEmpty()){
            throw new  ResourceNotFoundException("Pedido não encontrado. ID: " + id);
        }

        pedido.get().setStatus(Status.PAGO);
        pedidoRepository.save(pedido.get());
    }

    private void mapDtoToPedido(PedidoRequestDto requestDto, Pedido pedido) {

        pedido.setNome(requestDto.getNome());
        pedido.setCpf(requestDto.getCpf());

        for (ItemDoPedidoRequestDto itemDTO : requestDto.getItens()){

            ItemDoPedido itemPedido = new ItemDoPedido();
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setDescricao(itemDTO.getDescricao());
            itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());
            itemPedido.setPedido(pedido);
            pedido.getItens().add(itemPedido);
        }
    }
}
