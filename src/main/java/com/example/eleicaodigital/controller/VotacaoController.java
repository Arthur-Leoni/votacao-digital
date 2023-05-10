package com.example.eleicaodigital.controller;

import com.example.eleicaodigital.exceptions.*;
import com.example.eleicaodigital.model.*;
import com.example.eleicaodigital.service.PautaService;
import com.example.eleicaodigital.service.VotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

//TODO Lembrar dos testes unitarios e logs
//TODO COLOCAR MAPA na PROPERTIES
//TODO DIVIDIR CONTROLER VOTO e PAUTA
@RestController
@RequestMapping("/pautas")
public class VotacaoController {

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
    public ResponseEntity<String> iniciarVotacao(@PathVariable String pautaId,
                                             @RequestParam(required = false, defaultValue = "1") long duracaoMinutos) {
        try {
            pautaService.abrirVotacao(duracaoMinutos, pautaId);
            return ResponseEntity.ok().body("Votacao iniciada!");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SessaoDeVotacaoJaIniciadaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            // TODO logar a exceção para fins de diagnóstico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/votar")
    public ResponseEntity<String> votar(@RequestBody VotoDTO votoDTO) {
        // TODO validarCpf(cpf); Colocar liga e desliga via config

        try {
            //TODO criar um toEntity
            Voto novoVoto = Voto.builder()
                    .pautaId(votoDTO.getPautaId())
                    .voto(votoDTO.getVoto())
                    .cpfAssociado(votoDTO.getCpf())
                    .build();
            votoService.registrarVoto(novoVoto);

            // Retorna mensagem de sucesso
            return ResponseEntity.ok().body("Voto registrado com sucesso.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SessaoDeVotacaoEncerradaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (CpfJaVotouException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            // TODO logar a exceção para fins de diagnóstico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<?> buscarResultado(@PathVariable String pautaId) {
        try {
            VotoResultadoResponse resultado = votoService.buscarResultado(pautaId);
            return ResponseEntity.ok().body(resultado);
        } catch (VotacaoPendenteException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (SessaoDeVotacaoEncerradaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            // TODO logar a exceção para fins de diagnóstico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}