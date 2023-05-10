package com.example.eleicaodigital.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Data
@Builder
public class Pauta {

    @Id
    private String id;

    private String titulo;

    private Sessao sessao = new Sessao();

    public void abrirVotacao(long duracaoMinutos) {
        this.getSessao().setFimSessao(LocalDateTime.now().plusMinutes(duracaoMinutos));
        this.getSessao().setSessaoAberta(true);
    }

}