package com.github.cidarosa.ms.pedidos.service;

import com.github.cidarosa.ms.pedidos.dto.ItemDoPedidoDto;
import com.github.cidarosa.ms.pedidos.dto.PedidoDto;
import com.github.cidarosa.ms.pedidos.entities.ItemDoPedido;
import com.github.cidarosa.ms.pedidos.entities.Pedido;
import com.github.cidarosa.ms.pedidos.entities.Status;
import com.github.cidarosa.ms.pedidos.exceptions.ResourceNotFoundException;
import com.github.cidarosa.ms.pedidos.repositories.ItemDoPedidoRepository;
import com.github.cidarosa.ms.pedidos.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemDoPedidoRepository itemDoPedidoRepository;

    @Transactional(readOnly = true)
    public List<PedidoDto> findAllPedidos(){

        return pedidoRepository.findAll()
                .stream().map(PedidoDto::new).toList();
    }

    @Transactional(readOnly = true)
    public PedidoDto findPedidoById(Long id){

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Recurso não encontrado. ID: "+id)
        );
        return new PedidoDto(pedido);
    }

    @Transactional
    public PedidoDto savePedido(PedidoDto pedidoDto){

        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(pedidoDto, pedido);
        pedido.calcularValorTotalDoPedido();
        pedido = pedidoRepository.save(pedido);
        return new PedidoDto(pedido);
    }

    private void mapDtoToPedido(PedidoDto pedidoDto, Pedido pedido) {
        pedido.setNome(pedidoDto.getNome());
        pedido.setCpf(pedidoDto.getCpf());

        for (ItemDoPedidoDto itemDto : pedidoDto.getItens()){
            ItemDoPedido itemPedido = new ItemDoPedido();
            itemPedido.setQuantidade(itemDto.getQuantidade());
            itemPedido.setDescricao(itemDto.getDescricao());
            itemPedido.setPrecoUnitario(itemDto.getPrecoUnitario());
            itemPedido.setPedido(pedido);
            pedido.getItens().add(itemPedido);
        }
    }

    @Transactional
    public PedidoDto updatePedido(Long id, PedidoDto pedidoDto){

        try{
            Pedido pedido = pedidoRepository.getReferenceById(id);
            pedido.getItens().clear();
            pedido.setData(LocalDate.now());
            pedido.setStatus(Status.CRIADO);
            mapDtoToPedido(pedidoDto,pedido);
            pedido.calcularValorTotalDoPedido();
            pedido = pedidoRepository.save(pedido);
            return new PedidoDto(pedido);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Recurso não encontrado. ID: "+id);
        }
    }

    @Transactional
    public void  deletePedidoById(Long id){

        if (!pedidoRepository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado. ID: "+id);
        }
        pedidoRepository.deleteById(id);
    }
}
