package com.example.votacaodigital.controller;

import com.example.votacaodigital.exceptions.CpfInvalidoException;
import com.example.votacaodigital.model.CreatePautaDTO;
import com.example.votacaodigital.model.Pauta;
import com.example.votacaodigital.service.PautaService;
import com.example.votacaodigital.util.ValidadorCPF;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${votacaocontroller.mapping}/v2")
@Log4j2
public class VotacaoControllerV2 {

    @Autowired
    private PautaService pautaService;

    @ApiOperation("Cria uma nova pauta validando o cpf")
    @PostMapping
    public ResponseEntity<?> criarPautaV2(@RequestBody CreatePautaDTO createPautaRequest, @RequestParam String cpf) {
        // Implementação da versão 2 que inclui validacao do cpf para criacao de pauta
        try{
            validaCPF(cpf);
            Pauta novaPauta = pautaService.save(createPautaRequest.toEntity());
            log.info("[VotacaoControllerV2] Nova pauta criada: "+ novaPauta.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPauta);
        }catch (CpfInvalidoException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("pauta não pode ser criada "+ e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    private void validaCPF(String cpf) throws CpfInvalidoException {
        if(!ValidadorCPF.validarCPF(cpf)) {
            throw new CpfInvalidoException();
        }

    }

}
