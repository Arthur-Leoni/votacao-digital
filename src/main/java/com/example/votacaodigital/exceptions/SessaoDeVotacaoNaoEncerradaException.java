package com.example.votacaodigital.exceptions;

public class SessaoDeVotacaoNaoEncerradaException extends RuntimeException {
    public SessaoDeVotacaoNaoEncerradaException() {
        super("A sessão de votação para esta pauta não foi encerrada.");
    }
}
