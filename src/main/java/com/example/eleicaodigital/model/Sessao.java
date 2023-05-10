package com.example.eleicaodigital.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Sessao {

    private boolean sessaoAberta = false;

    private LocalDateTime fimSessao = null;

}
