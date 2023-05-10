package com.example.eleicaodigital.exceptions;

public class SessaoDeVotacaoEncerradaException extends RuntimeException {
    public SessaoDeVotacaoEncerradaException() {
        super("A sessão de votação para esta pauta já foi encerrada.");
    }
}
