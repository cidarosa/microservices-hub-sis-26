package com.github.cida.ms.pagamentos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cida.ms.pagamentos.dto.PagamentoResponseDTO;
import com.github.cida.ms.pagamentos.entities.Pagamento;
import com.github.cida.ms.pagamentos.exceptions.ResourceNotFoundException;
import com.github.cida.ms.pagamentos.service.PagamentoService;
import com.github.cida.ms.pagamentos.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PagamentoService pagamentoService;
    private Pagamento pagamento;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() {

        existingId = 1l;
        nonExistingId = Long.MAX_VALUE;

        pagamento = Factory.createPagamento();
    }

    @Test
    void findAllPagamentosShouldReturnListPagamentoDTO() throws Exception {

        PagamentoResponseDTO inputDto = new PagamentoResponseDTO(pagamento);
        List<PagamentoResponseDTO> list = List.of(inputDto);
        Mockito.when(pagamentoService.findAllPagamento()).thenReturn(list);

        ResultActions result = mockMvc.perform(get("/pagamentos")
                .accept(MediaType.APPLICATION_JSON)); // request: Accept
        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("$[0].id").value(pagamento.getId()));
        result.andExpect(jsonPath("$[0].valor").value(pagamento.getValor().doubleValue()));

        Mockito.verify(pagamentoService).findAllPagamento();
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void findPagamentoByIdShouldReturnPagamentoDTOWhenIdExists() throws Exception {

        PagamentoResponseDTO responseDTO = new PagamentoResponseDTO(pagamento);
        Mockito.when(pagamentoService.findPagamentoById(existingId)).thenReturn(responseDTO);

        mockMvc.perform(get("/pagamentos/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.valor").value(pagamento.getValor().doubleValue()))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).findPagamentoById(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void findPagamentoByIdShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(pagamentoService.findPagamentoById(nonExistingId))
                .thenThrow(new ResourceNotFoundException("Recurso não encontrado. ID: " + nonExistingId));

        mockMvc.perform(get("/pagamentos/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(pagamentoService).findPagamentoById(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void createPagamentoShouldReturn201WhenValid() throws Exception {

        PagamentoResponseDTO requestDTO = new PagamentoResponseDTO(Factory.createPagamentoSemId());
        // Bean objectMapper para converter JAVA para JSON
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);
        PagamentoResponseDTO responseDTO = new PagamentoResponseDTO(pagamento);
        Mockito.when(pagamentoService.save(any(PagamentoResponseDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON) // request Content-Type
                        .accept(MediaType.APPLICATION_JSON) //request Accept
                        .content(jsonRequestBody)) //request body
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) // response
                .andExpect(jsonPath("$.id").value(pagamento.getId()))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.valor").value(pagamento.getValor().doubleValue()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).save(any(PagamentoResponseDTO.class));
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void createPagamentoShouldReturn422WhenInvalid() throws Exception {

        Pagamento pagamentoInvalido = Factory.createPagamentoSemId();
        pagamentoInvalido.setValor(BigDecimal.valueOf(0));
        pagamentoInvalido.setNome(null);

        PagamentoResponseDTO requestDTO = new PagamentoResponseDTO(pagamentoInvalido);

        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);

        PagamentoResponseDTO responseDTO = new PagamentoResponseDTO(pagamentoInvalido);

        Mockito.when(pagamentoService.save(any(PagamentoResponseDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))

                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        Mockito.verifyNoInteractions(pagamentoService);
    }

    @Test
    void updatePagamentoShouldReturn200WhenValid() throws Exception {

        PagamentoResponseDTO requestDTO = new PagamentoResponseDTO(Factory.createPagamento());
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);
        PagamentoResponseDTO responseDTO = new PagamentoResponseDTO(pagamento);

        Mockito.when(pagamentoService.update(eq(existingId), any(PagamentoResponseDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/pagamentos/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).update(eq(existingId), any(PagamentoResponseDTO.class));
        Mockito.verifyNoMoreInteractions(pagamentoService);

    }

    @Test
    void updatePagamentoShouldReturn422WhenInvalid() throws Exception {

        Pagamento pagamentoInvalido = Factory.createPagamento();
        pagamentoInvalido.setValor(BigDecimal.ZERO);
        pagamentoInvalido.setNome(null);
        PagamentoResponseDTO requestDTO = new PagamentoResponseDTO(pagamentoInvalido);
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(put("/pagamentos/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        Mockito.verifyNoInteractions(pagamentoService);
    }

    @Test
    void updatePagamentoShouldReturn404WhenIdDoesNotExist() throws Exception {

        PagamentoResponseDTO requestDTO = new PagamentoResponseDTO(Factory.createPagamento());
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);

        Mockito.when(pagamentoService.update(eq(nonExistingId), any(PagamentoResponseDTO.class)))
                .thenThrow(new ResourceNotFoundException("Recurso não encontrado. ID: " + nonExistingId));

        mockMvc.perform(put("/pagamentos/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))

                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(pagamentoService).update(eq(nonExistingId), any(PagamentoResponseDTO.class) );
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void deletePagamentoShouldReturn204WhenIdExists() throws Exception {

        Mockito.doNothing().when(pagamentoService).deletePagamento(existingId);

        mockMvc.perform(delete("/pagamentos/{id}", existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(pagamentoService).deletePagamento(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void deletePagamentoShouldReturn404WhenIdDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Recurso não encontrado. ID: " + nonExistingId))
                .when(pagamentoService).deletePagamento(nonExistingId);

        mockMvc.perform(delete("/pagamentos/{id}", nonExistingId))
                .andExpect(status().isNotFound());

        Mockito.verify(pagamentoService).deletePagamento(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

}
