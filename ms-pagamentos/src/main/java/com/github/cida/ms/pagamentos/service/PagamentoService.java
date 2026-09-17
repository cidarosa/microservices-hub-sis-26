package com.github.cida.ms.pagamentos.service;

import com.github.cida.ms.pagamentos.client.PedidoClient;
import com.github.cida.ms.pagamentos.dto.PagamentoRequestDTO;
import com.github.cida.ms.pagamentos.dto.PagamentoResponseDTO;
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
    public PagamentoResponseDTO alterarStatusDoPagamento(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pagamento não encontrado. ID: " + id)
        );

        pagamento.setStatus(Status.CONFIRMACAO_PENDENTE);
        pagamento = pagamentoRepository.save(pagamento);
        return new PagamentoResponseDTO(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO confirmarPagamentoDoPedido(Long id) {

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

        return new PagamentoResponseDTO(pagamento);
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> findAllPagamento() {

        return pagamentoRepository.findAll()
                .stream()
                .map(PagamentoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDTO findPagamentoById(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new PagamentoResponseDTO(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO save(PagamentoRequestDTO requestDTO) {

        Pagamento pagamento = new Pagamento();
        mapDtoToPagamento(requestDTO, pagamento);
        pagamento.setStatus(Status.CRIADO);
        pagamento = pagamentoRepository.save(pagamento);
        return new PagamentoResponseDTO(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO update(Long id, PagamentoRequestDTO requestDTO) {

        try {
            Pagamento pagamento = pagamentoRepository.getReferenceById(id);

            if (pagamento.getStatus().equals(Status.APROVADO)) {
                throw new PagamentoAprovadoException(
                        String.format("Pagamento id %d já está APROVADO e não pode ser alterado", id)
                );
            }
            mapDtoToPagamento(requestDTO, pagamento);
            pagamento.setStatus(Status.CRIADO);
            pagamento = pagamentoRepository.save(pagamento);
            return new PagamentoResponseDTO(pagamento);
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

    private void mapDtoToPagamento(PagamentoRequestDTO requestDTO, Pagamento pagamento) {

        pagamento.setValor(requestDTO.getValor());
        pagamento.setNome(requestDTO.getNome());
        pagamento.setNumeroCartao(requestDTO.getNumeroCartao());
        pagamento.setValidade(requestDTO.getValidade());
        pagamento.setCodigoSeguranca(requestDTO.getCodigoSeguranca());
        pagamento.setPedidoId(requestDTO.getPedidoId());
    }

}
