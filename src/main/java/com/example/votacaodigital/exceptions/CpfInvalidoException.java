package com.example.votacaodigital.exceptions;

public class CpfInvalidoException extends Exception {
    public CpfInvalidoException() {
        super("Este CPF esta invalido.");
    }

}