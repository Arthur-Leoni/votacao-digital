package com.example.votacaodigital.exceptions;

public class VotacaoPendenteException extends RuntimeException {
    public VotacaoPendenteException() {
        super("A sessão de votação ainda não foi iniciada");
    }
}
