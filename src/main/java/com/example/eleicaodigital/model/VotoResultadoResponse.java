package com.example.eleicaodigital.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VotoResultadoResponse {

    private String pautaId;
    private long totalVotos;
    private long totalSIM;
    private long totalNAO;

}
