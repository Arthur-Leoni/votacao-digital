package com.example.votacaodigital.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
public class Pauta {

    @Id
    private String id;

    private String titulo;

    private Sessao sessao = new Sessao();

    public void abrirVotacao(long duracaoMinutos) {
        this.getSessao().setFimSessao(LocalDateTime.now().plusMinutes(duracaoMinutos));
    }

    public boolean isSessaoAberta() {
        if (this.getSessao().getFimSessao() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(this.getSessao().getFimSessao());
    }

    public boolean isSessaoFinalizada() {
        if (this.getSessao().getFimSessao() != null && LocalDateTime.now().isAfter(this.getSessao().getFimSessao())) {
            return true;
        }
        return false;
    }

 }