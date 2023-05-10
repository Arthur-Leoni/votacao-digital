package com.example.eleicaodigital.service;

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

    public Voto save(Voto voto) {
        return votoRepository.save(voto);
    }


    public Voto registrarVoto(Voto novoVoto) {
        //TODO trazer validacoes e lancar excessoes aqui
        return save(novoVoto);
    }

    public VotoResultadoResponse buscarResultado(String pautaId){
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

}
