package com.example.votacaodigital.service;

import com.example.votacaodigital.exceptions.*;
import com.example.votacaodigital.feign.CpfApiClient;
import com.example.votacaodigital.model.Pauta;
import com.example.votacaodigital.model.Voto;
import com.example.votacaodigital.model.VotoEnum;
import com.example.votacaodigital.model.VotoResultadoResponse;
import com.example.votacaodigital.repository.VotoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class VotoService {

    @Autowired
    private CpfApiClient cpfApiClient;
    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private PautaService pautaService;

    public Voto save(Voto voto) {
        return votoRepository.save(voto);
    }

    public Voto registrarVoto(Voto novoVoto) throws CpfJaVotouException, CpfInvalidoException {
        //Devera ser usado criptografia no cpf para nao ficar exposto
        log.info("Registrando voto do cpf: "+ novoVoto.getCpfAssociado());

        //Busca e valida se existe a pauta
        Pauta pauta = pautaService.findById(novoVoto.getPautaId());

        //Valida se sessao esta aberta
        validaSessaoAberta(pauta);

        //Validacao se cpf ja votou
        validaVotoCpf(novoVoto.getCpfAssociado(), novoVoto.getPautaId());

        //valida se cpf é valido (ultima validaçao para evitar chamadas externas desnecessarias
        validaCpf(novoVoto.getCpfAssociado());

        //Salva o voto
        return save(novoVoto);
    }

    private void validaCpf(String cpf) throws CpfInvalidoException {
        if(!cpfApiClient.verificarCpf(cpf)){
            log.info("Este CPF esta invalido: "+ cpf);
            throw new CpfInvalidoException();
        }
    }
    public VotoResultadoResponse buscarResultado(String pautaId){
        log.info("Buscando resultado referente a pauta: "+ pautaId);

        Pauta pauta = pautaService.findById(pautaId);
        validaSeVotacaoJaFoiRealizada(pauta);
        validaFimDaSessao(pauta);;
        List<Voto> votos = votoRepository.findAllByPautaId(pautaId);

        long totalVotos = votos.stream().count();
        long totalNao = votos.stream().filter(v -> v.getVoto().equals(VotoEnum.NAO)).count();
        long totalSIM = votos.stream().filter(v -> v.getVoto().equals(VotoEnum.SIM)).count();

        return VotoResultadoResponse.builder()
                .pautaId(pautaId)
                .totalVotos(totalVotos)
                .totalNAO(totalNao)
                .totalSIM(totalSIM)
                .build();
    }

    private void validaVotoCpf(String cpf, String pautaId) throws CpfJaVotouException {
        if(votoRepository.findByCpfAssociadoAndPautaId(cpf, pautaId).isPresent()){
            log.info("Este CPF já votou e não pode votar novamente. cpf: "+ cpf);
            throw new CpfJaVotouException();
        }
    }

    private void validaFimDaSessao(Pauta pauta) {
        //Valida se a sessao ja foi finalizada
        if(!pauta.isSessaoFinalizada()){
            log.info("A sessão de votação para esta pauta não foi finalizada: "+ pauta.getId());
            throw new SessaoDeVotacaoNaoEncerradaException();
        }
    }

    private void validaSeVotacaoJaFoiRealizada(Pauta pauta) {
        //Valida votacao ja foi realizada
        if(pauta.getSessao().getFimSessao() == null){
            log.info("A sessão de votação ainda não foi iniciada para pauta: "+ pauta.getId());
            throw new VotacaoPendenteException();
        }
    }

    private void validaSessaoAberta(Pauta pauta) {
        //Sessao deve estar aberta
        if (!pauta.isSessaoAberta()) {
            log.info("A sessão de votação para esta pauta já foi encerrada: "+ pauta.getId());
            throw new SessaoDeVotacaoEncerradaException();
        }
    }
}
