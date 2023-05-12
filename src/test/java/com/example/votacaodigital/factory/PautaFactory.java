package com.example.votacaodigital.factory;

import com.example.votacaodigital.model.Pauta;
import com.example.votacaodigital.model.Sessao;

import java.time.LocalDateTime;

public class PautaFactory {

    public static final String PAUTA_COM_SESSAO_ABERTA_ID = "2";
    public static final String PAUTA_COM_SESSAO_FINALIZADA_ID = "3";
    public static final String PAUTA_COM_VOTACAO_NAO_REALIZADA_ID = "4";
    public static final String PAUTA_COM_SESSAO_ABERTA_TITULO = "Pauta com sessao aberta";
    public static final String PAUTA_COM_SESSAO_FINALIZADA_TITULO = "Pauta com sessão finalizada";
    public static final String PAUTA_COM_VOTACAO_NAO_REALIZADA_TITULO = "Pauta com votação não realizada";

    public static Pauta criarPautaComSessaoAberta() {
        Sessao sessao = new Sessao();
        sessao.setFimSessao(LocalDateTime.now().plusMinutes(30));

        return Pauta.builder()
                .id(PAUTA_COM_SESSAO_ABERTA_ID)
                .titulo(PAUTA_COM_SESSAO_ABERTA_TITULO)
                .sessao(sessao)
                .build();
    }

    public static Pauta criarPautaComSessaoFinalizada() {
        Sessao sessao = new Sessao();
        sessao.setFimSessao(LocalDateTime.now().minusMinutes(30));

        return Pauta.builder()
                .id(PAUTA_COM_SESSAO_FINALIZADA_ID)
                .titulo(PAUTA_COM_SESSAO_FINALIZADA_TITULO)
                .sessao(sessao)
                .build();
    }

    public static Pauta criarPautaComVotacaoNaoRealizada() {
        return Pauta.builder()
                .id(PAUTA_COM_VOTACAO_NAO_REALIZADA_ID)
                .titulo(PAUTA_COM_VOTACAO_NAO_REALIZADA_TITULO)
                .sessao(new Sessao())
                .build();
    }
}
