package com.example.eleicaodigital.service;

import com.example.eleicaodigital.model.Pauta;
import com.example.eleicaodigital.repository.PautaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PautaService {

    @Autowired
    private PautaRepository pautaRepository;

    public Pauta save(Pauta pauta) {
        return pautaRepository.save(pauta);
    }

    public Optional<Pauta> findById(String pautaId) {
        return pautaRepository.findById(pautaId);
    }

    public Pauta abrirVotacao(long duracaoMinutos, Pauta pauta) {
        pauta.abrirVotacao(duracaoMinutos);
        return save(pauta);
    }

}
