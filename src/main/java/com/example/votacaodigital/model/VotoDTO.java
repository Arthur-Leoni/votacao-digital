package com.example.votacaodigital.model;

import lombok.Data;

@Data
public class VotoDTO {

    private String cpf;
    private VotoEnum voto;
    private String pautaId;
}
