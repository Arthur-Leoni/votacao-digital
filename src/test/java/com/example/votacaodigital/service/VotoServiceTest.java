package com.example.votacaodigital.service;

import com.example.votacaodigital.exceptions.*;
import com.example.votacaodigital.factory.PautaFactory;
import com.example.votacaodigital.feign.CpfApiClient;
import com.example.votacaodigital.model.Pauta;
import com.example.votacaodigital.model.Voto;
import com.example.votacaodigital.model.VotoEnum;
import com.example.votacaodigital.model.VotoResultadoResponse;
import com.example.votacaodigital.repository.VotoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private CpfApiClient cpfApiClient;

    @InjectMocks
    private VotoService votoService;

    private Voto voto;

    @BeforeEach
    public void setUp() {
        voto = Voto.builder()
                .cpfAssociado("111.111.111-11")
                .voto(VotoEnum.SIM)
                .pautaId("1")
                .id("1")
                    .build();
    }

    @Test
    public void shouldSaveVoto_whenVotoValido() {
        when(votoRepository.save(voto)).thenReturn(voto);

        Voto savedVoto = votoService.save(voto);

        Assertions.assertNotNull(savedVoto);
        Assertions.assertEquals(voto, savedVoto);
    }

    @Test
    public void shouldRegistrarVoto_whenSessaoAbertaECpfNaoVoutou() throws CpfJaVotouException, CpfInvalidoException {
        Pauta pauta = PautaFactory.criarPautaComSessaoAberta();

        when(pautaService.findById(anyString())).thenReturn(pauta);
        when(votoRepository.findByCpfAssociadoAndPautaId(anyString(), anyString())).thenReturn(Optional.empty());
        when(votoRepository.save(voto)).thenReturn(voto);
        when(cpfApiClient.verificarCpf(anyString())).thenReturn(true);

        Voto registeredVoto = votoService.registrarVoto(voto);

        Assertions.assertNotNull(registeredVoto);
        Assertions.assertEquals(voto, registeredVoto);
    }

    @Test
    public void shouldThrowException_whenSessaoNaoAberta() {
        Pauta pautaComSessaoFinalizada = PautaFactory.criarPautaComSessaoFinalizada();

        when(pautaService.findById(anyString())).thenReturn(pautaComSessaoFinalizada);

        Assertions.assertThrows(SessaoDeVotacaoEncerradaException.class, () -> votoService.registrarVoto(voto));
    }

    @Test
    public void shouldThrowException_whenCpfJaVoted() {
        Pauta pauta = PautaFactory.criarPautaComSessaoAberta();

        when(pautaService.findById(anyString())).thenReturn(pauta);
        when(votoRepository.findByCpfAssociadoAndPautaId(anyString(), anyString())).thenReturn(Optional.of(voto));

        Assertions.assertThrows(CpfJaVotouException.class, () -> votoService.registrarVoto(voto));
    }

    @Test
    public void shouldReturnResultado_whenVotacaoRealizadaESessaoFinalizada() {
        Pauta pauta = PautaFactory.criarPautaComSessaoFinalizada();

        VotoResultadoResponse expected = VotoResultadoResponse.builder()
                .pautaId("3")
                .totalVotos(1)
                .totalNAO(0)
                .totalSIM(1).build();

        when(pautaService.findById(anyString())).thenReturn(pauta);
        when(votoRepository.findAllByPautaId(anyString())).thenReturn(Arrays.asList(voto));

        VotoResultadoResponse response =
                votoService.buscarResultado(pauta.getId());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expected, response);
    }

    @Test
    public void shouldThrowException_whenVotacaoNaoRealizada() {
        Pauta pauta = PautaFactory.criarPautaComVotacaoNaoRealizada();

        when(pautaService.findById(anyString())).thenReturn(pauta);

        Assertions.assertThrows(VotacaoPendenteException.class, () -> votoService.buscarResultado(pauta.getId()));
    }

    @Test
    public void shouldThrowException_whenSessaoNaoFinalizada() {
        Pauta pauta = PautaFactory.criarPautaComSessaoAberta();

        when(pautaService.findById(anyString())).thenReturn(pauta);

        Assertions.assertThrows(SessaoDeVotacaoNaoEncerradaException.class, () -> votoService.buscarResultado(pauta.getId()));
    }
}
