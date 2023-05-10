package com.example.eleicaodigital.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Sessao {

    private LocalDateTime fimSessao;


    public Sessao() {
        this.fimSessao = null;
    }


}
