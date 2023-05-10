package com.example.eleicaodigital.controller;

import com.example.eleicaodigital.model.*;
import com.example.eleicaodigital.service.PautaService;
import com.example.eleicaodigital.service.VotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import java.time.LocalDateTime;
import java.util.Optional;

//TODO Lembrar dos testes unitarios e logs
//TODO COLOCAR MAPA na PROPERTIES
//TODO DIVIDIR CONTROLER VOTO e PAUTA
@RestController
@RequestMapping("/pautas")
public class PautaController {

    @Autowired
    private PautaService pautaService;

    @Autowired
    private VotoService votoService;

    @PostMapping
    public ResponseEntity<Pauta> criarPauta(@RequestBody CreatePautaDTO createPautaRequest) {
        Pauta novaPauta = pautaService.save(createPautaRequest.toDomain());
        return ResponseEntity.created(URI.create("/pautas/" + novaPauta.getId())).body(novaPauta);
    }

    @PostMapping("/{pautaId}/iniciarVotacao")
    public ResponseEntity<Void> iniciarVotacao(@PathVariable String pautaId,
                                             @RequestParam(required = false, defaultValue = "1") long duracaoMinutos) {
        Optional<Pauta> optionalPauta = pautaService.findById(pautaId);
        //TODO VERIFICAR SE A VOTACAO JA NAO FOI FEITA
        //TODO verificar se a votacao ja nao esta em andamento
        if (optionalPauta.isPresent()) {
            pautaService.abrirVotacao(duracaoMinutos, optionalPauta.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/votar")
    public ResponseEntity<String> votar(@RequestBody VotoDTO votoDTO) {
        Optional<Pauta> optionalPauta = pautaService.findById(votoDTO.getPautaId());
        Pauta pauta;
        if (optionalPauta.isPresent()) {
            pauta = optionalPauta.get();
        } else {
            //TODO Logar
            return ResponseEntity.notFound().build();
        }

        // verificar se a pauta está aberta para votação
        if (pauta == null || pauta.getSessao().isSessaoAberta() == false || pauta.getSessao().getFimSessao().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("A pauta não está aberta para votação.");
        }

        //TODO Verificar se ja votou
        // verificar se o associado já votou na pauta


        // verificar se o CPF é válido
        // TODO validarCpf(cpf); Colocar liga e desliga via config
        boolean cpfValido = true;
        if (!cpfValido) {
            return ResponseEntity.badRequest().body("CPF inválido.");
        }

        //TODO Extrair validates



        Voto novoVoto = Voto.builder()
                .pautaId(pauta.getId())
                .voto(votoDTO.getVoto())
                .cpfAssociado(votoDTO.getCpf())
                .build();

        votoService.registrarVoto(novoVoto);

        // retornar mensagem de sucesso
        return ResponseEntity.ok().body("Voto registrado com sucesso.");
    }

    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<VotoResultadoResponse> buscarResultado(@PathVariable String pautaId) {
        VotoResultadoResponse resultado = votoService.buscarResultado(pautaId);
        return ResponseEntity.ok().body(resultado);
    }

}