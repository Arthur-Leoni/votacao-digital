package com.example.votacaodigital.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Voto {

    @Id
    private String id;

    private String pautaId;

    private String cpfAssociado;

    private VotoEnum voto;

}