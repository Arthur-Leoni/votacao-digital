package com.example.eleicaodigital.service;

import com.example.eleicaodigital.exceptions.NotFoundException;
import com.example.eleicaodigital.exceptions.SessaoDeVotacaoJaIniciadaException;
import com.example.eleicaodigital.model.Pauta;
import com.example.eleicaodigital.repository.PautaRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PautaService {

    @Autowired
    private PautaRepository pautaRepository;

    public Pauta save(Pauta pauta) {
        return pautaRepository.save(pauta);
    }

    public Pauta findById(String pautaId) {
        log.info("Buscando pauta: "+ pautaId);
        return pautaRepository.findById(pautaId).orElseThrow(() -> new NotFoundException("Pauta não encontrada"));
    }

    public Pauta abrirVotacao(long duracaoMinutos, String pautaId) {
        log.info("Iniciando votação para pauta: "+ pautaId);
        //Busca Pauta
        Pauta pauta = findById(pautaId);

        //valida se sessao ja não esta aberta para votacao
        validaSessaoJaAberta(pauta);

        //Abri votacao e persiste
        pauta.abrirVotacao(duracaoMinutos);
        return save(pauta);
    }

    private void validaSessaoJaAberta(Pauta pauta) {
        if (pauta.isSessaoAberta()) {
            log.info("A sessão de votação para esta pauta já foi iniciada: "+ pauta.getId());
            throw new SessaoDeVotacaoJaIniciadaException();
        }
    }

}
