package com.example.eleicaodigital.model;

import lombok.Data;

@Data
public class CreatePautaDTO {

    private String titulo;

    public Pauta toDomain(){
        return Pauta.builder()
                .titulo(this.titulo)
                .sessao(new Sessao())
                .build();
    }
}
