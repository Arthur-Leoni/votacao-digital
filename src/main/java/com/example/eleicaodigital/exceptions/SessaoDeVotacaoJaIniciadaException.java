package com.example.eleicaodigital.exceptions;

public class SessaoDeVotacaoJaIniciadaException extends RuntimeException {
    public SessaoDeVotacaoJaIniciadaException() {
        super("A sessão de votação para esta pauta já foi iniciada.");
    }
}
