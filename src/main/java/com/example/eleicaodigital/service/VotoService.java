package com.example.eleicaodigital.service;

import com.example.eleicaodigital.exceptions.CpfJaVotouException;
import com.example.eleicaodigital.exceptions.SessaoDeVotacaoEncerradaException;
import com.example.eleicaodigital.exceptions.SessaoDeVotacaoJaIniciadaException;
import com.example.eleicaodigital.exceptions.VotacaoPendenteException;
import com.example.eleicaodigital.model.Pauta;
import com.example.eleicaodigital.model.Voto;
import com.example.eleicaodigital.model.VotoEnum;
import com.example.eleicaodigital.model.VotoResultadoResponse;
import com.example.eleicaodigital.repository.VotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VotoService {

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private PautaService pautaService;

    public Voto save(Voto voto) {
        return votoRepository.save(voto);
    }

    public Voto registrarVoto(Voto novoVoto) throws CpfJaVotouException {
        //Busca e valida se existe a pauta
        Pauta pauta = pautaService.findById(novoVoto.getPautaId());

        //Valida se sessao esta aberta
        validaSessaoAberta(pauta);

        //Validacao se cpf ja votou
        validaVotoCpf(novoVoto.getCpfAssociado(), novoVoto.getPautaId());

        //Salva o voto
        return save(novoVoto);
    }

    public VotoResultadoResponse buscarResultado(String pautaId){
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
            throw new CpfJaVotouException();
        }
    }

    private void validaFimDaSessao(Pauta pauta) {
        //Valida se a sessao ja foi finalizada
        if(!pauta.isSessaoFinalizada()){
            throw new SessaoDeVotacaoEncerradaException();
        }
    }

    private void validaSeVotacaoJaFoiRealizada(Pauta pauta) {
        //Valida votacao ja foi realizada
        if(pauta.getSessao().getFimSessao() == null){
            throw new VotacaoPendenteException();
        }
    }

    private void validaSessaoAberta(Pauta pauta) {
        //Sessao deve estar aberta
        if (!pauta.isSessaoAberta()) {
            throw new SessaoDeVotacaoEncerradaException();
        }
    }
}
