package com.github.cida.ms.pagamentos.service;

import com.github.cida.ms.pagamentos.client.PedidoClient;
import com.github.cida.ms.pagamentos.dto.PagamentoDTO;
import com.github.cida.ms.pagamentos.entities.Pagamento;
import com.github.cida.ms.pagamentos.entities.Status;
import com.github.cida.ms.pagamentos.exceptions.PagamentoAprovadoException;
import com.github.cida.ms.pagamentos.exceptions.ResourceNotFoundException;
import com.github.cida.ms.pagamentos.repository.PagamentoRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoClient pedidoClient;

    @Transactional
    public PagamentoDTO alterarStatusDoPagamento(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pagamento não encontrado. ID: " + id)
        );

        pagamento.setStatus(Status.CONFIRMACAO_PENDENTE);
        pagamento = pagamentoRepository.save(pagamento);
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO confirmarPagamentoDoPedido(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id));

        pagamento.setStatus(Status.APROVADO);
        pagamentoRepository.save(pagamento);

        try {
            pedidoClient.confirmarPagamento(pagamento.getPedidoId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Pedido não encontrado. ID: " + id);
        } catch (FeignException e) {
            throw new RuntimeException("Falha ao se comunicar com ms-pedidos");
        }

        return new PagamentoDTO(pagamento);
    }

    @Transactional(readOnly = true)
    public List<PagamentoDTO> findAllPagamento() {

        return pagamentoRepository.findAll()
                .stream()
                .map(PagamentoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagamentoDTO findPagamentoById(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO save(PagamentoDTO pagamentoDTO) {

        Pagamento pagamento = new Pagamento();
        mapDtoToPagamento(pagamentoDTO, pagamento);
        pagamento.setStatus(Status.CRIADO);
        pagamento = pagamentoRepository.save(pagamento);
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO update(Long id, PagamentoDTO pagamentoDTO) {

        try {
            Pagamento pagamento = pagamentoRepository.getReferenceById(id);

            if (pagamento.getStatus().equals(Status.APROVADO)) {
                throw new PagamentoAprovadoException(
                        String.format("Pagamento id %d já está APROVADO e não pode ser alterado", id)
                );
            }
            mapDtoToPagamento(pagamentoDTO, pagamento);
            pagamento.setStatus(pagamentoDTO.getStatus());
            pagamento = pagamentoRepository.save(pagamento);
            return new PagamentoDTO(pagamento);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    @Transactional
    public void deletePagamento(Long id) {

        if (!pagamentoRepository.existsById(id)) {

            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        pagamentoRepository.deleteById(id);
    }

    private void mapDtoToPagamento(PagamentoDTO pagamentoDTO, Pagamento pagamento) {

        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setNome(pagamentoDTO.getNome());
        pagamento.setNumeroCartao(pagamentoDTO.getNumeroCartao());
        pagamento.setValidade(pagamentoDTO.getValidade());
        pagamento.setCodigoSeguranca(pagamentoDTO.getCodigoSeguranca());
        pagamento.setPedidoId(pagamentoDTO.getPedidoId());
    }

}
