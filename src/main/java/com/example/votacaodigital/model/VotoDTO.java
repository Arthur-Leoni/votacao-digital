package com.example.votacaodigital.model;

import lombok.Data;

@Data
public class VotoDTO {

    private String cpf;
    private VotoEnum voto;
    private String pautaId;

    public Voto toEntity() {
        return Voto.builder()
                .pautaId(this.getPautaId())
                .voto(this.getVoto())
                .cpfAssociado(this.getCpf())
                .build();
    }
}
