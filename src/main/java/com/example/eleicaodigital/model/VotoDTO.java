package com.example.eleicaodigital.model;

import lombok.Data;

@Data
public class VotoDTO {

    private String cpf;
    private VotoEnum voto;
    private String pautaId;
}
