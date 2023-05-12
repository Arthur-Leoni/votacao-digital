package com.example.votacaodigital.service;

import com.example.votacaodigital.exceptions.NotFoundException;
import com.example.votacaodigital.exceptions.SessaoDeVotacaoJaIniciadaException;
import com.example.votacaodigital.model.Pauta;
import com.example.votacaodigital.model.Sessao;
import com.example.votacaodigital.repository.PautaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class PautaServiceTest {
    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    private Pauta pauta;
    private String pautaId;
    private long duracaoMinutos;
    private LocalDateTime timeNow;

    @BeforeEach
    public void setUp() {
        pautaId = "pautaId";
        duracaoMinutos = 5L;
        pauta = new Pauta(pautaId, "teste", new Sessao());
        timeNow = LocalDateTime.now();
    }

    @Test
    void shouldReturnPauta_whenPautaExistir() {
        String pautaId = "1";
        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .titulo("teste").build();
        Mockito.when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        Pauta result = pautaService.findById(pautaId);

        assertEquals(pauta, result);
        Mockito.verify(pautaRepository, Mockito.times(1)).findById(pautaId);
    }

    @Test
    void shouldThrowsException_whenPautaNaoExistir() {
        String pautaId = "1";
        Mockito.when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pautaService.findById(pautaId));
        Mockito.verify(pautaRepository, Mockito.times(1)).findById(pautaId);
    }

    @Test
    public void shouldAbirVotacao_whenPautaExistir() {
        MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class);
        mockedStatic.when(LocalDateTime::now).thenReturn(timeNow);

        Mockito.when(pautaRepository.findById(pautaId)).thenReturn(Optional.ofNullable(pauta));
        Mockito.when(pautaRepository.save(pauta)).thenReturn(pauta);

        Pauta result = pautaService.abrirVotacao(duracaoMinutos, pautaId);

        assertNotNull(result);
        assertEquals(pauta, result);
        assertTrue(result.isSessaoAberta());
        Mockito.verify(pautaRepository, Mockito.times(1)).findById(pautaId);
        Mockito.verify(pautaRepository, Mockito.times(1)).save(pauta);
    }

    @Test
    public void shouldThrow_Exception_whenSessaoJaAberta() {
        pauta.abrirVotacao(duracaoMinutos);
        Mockito.when(pautaRepository.findById(pautaId)).thenReturn(Optional.ofNullable(pauta));

        assertThrows(SessaoDeVotacaoJaIniciadaException.class, () -> pautaService.abrirVotacao(duracaoMinutos, pautaId));

        Mockito.verify(pautaRepository, Mockito.times(1)).findById(pautaId);
        Mockito.verify(pautaRepository, Mockito.times(0)).save(pauta);
    }

}