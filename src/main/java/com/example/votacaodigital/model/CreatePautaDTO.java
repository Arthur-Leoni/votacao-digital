package com.example.votacaodigital.model;

import lombok.Data;

@Data
public class CreatePautaDTO {

    private String titulo;

    public Pauta toEntity(){
        return Pauta.builder()
                .titulo(this.titulo)
                .sessao(new Sessao())
                .build();
    }
}
