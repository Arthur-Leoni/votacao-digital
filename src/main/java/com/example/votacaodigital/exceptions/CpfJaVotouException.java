package com.example.votacaodigital.exceptions;

public class CpfJaVotouException extends Exception {
    public CpfJaVotouException() {
        super("Este CPF já votou e não pode votar novamente.");
    }

}