package com.example.eleicaodigital.exceptions;

public class CpfJaVotouException extends Exception {
    public CpfJaVotouException() {
        super("Este CPF já votou e não pode votar novamente.");
    }

}