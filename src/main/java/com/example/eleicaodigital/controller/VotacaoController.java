package com.example.eleicaodigital.controller;

import com.example.eleicaodigital.exceptions.*;
import com.example.eleicaodigital.model.*;
import com.example.eleicaodigital.service.PautaService;
import com.example.eleicaodigital.service.VotoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

//TODO Lembrar dos testes unitarios e logs
@RestController
@RequestMapping("/${votacaocontroller.mapping}")
@Log4j2
public class VotacaoController {

    @Autowired
    private PautaService pautaService;

    @Autowired
    private VotoService votoService;

    @PostMapping
    public ResponseEntity<Pauta> criarPauta(@RequestBody CreatePautaDTO createPautaRequest) {
        Pauta novaPauta = pautaService.save(createPautaRequest.toDomain());
        log.info("Nova pauta criada: "+ novaPauta.getId());
        return ResponseEntity.created(URI.create("/pautas/" + novaPauta.getId())).body(novaPauta);
    }

    @PostMapping("/{pautaId}/${votacaocontroller.iniciarvotacao}")
    public ResponseEntity<String> iniciarVotacao(@PathVariable String pautaId,
                                             @RequestParam(required = false, defaultValue = "1") long duracaoMinutos) {
        try {
            pautaService.abrirVotacao(duracaoMinutos, pautaId);
            return ResponseEntity.ok().body("Votacao iniciada!");
        } catch (NotFoundException e) {
            log.info("Pauta não encontrada: "+ pautaId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SessaoDeVotacaoJaIniciadaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("Votaçao não pode ser iniciada para pauta: "+ pautaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/${votacaocontroller.votar}")
    public ResponseEntity<String> votar(@RequestBody VotoDTO votoDTO) {
        // TODO validarCpf(cpf); Colocar liga e desliga via config

        try {
            //TODO criar um toEntity
            Voto novoVoto = Voto.builder()
                    .pautaId(votoDTO.getPautaId())
                    .voto(votoDTO.getVoto())
                    .cpfAssociado(votoDTO.getCpf())
                    .build();
            Voto voto = votoService.registrarVoto(novoVoto);

            // Retorna mensagem de sucesso
            log.info("Voto registrado com sucesso. voto: "+ voto.getId());
            return ResponseEntity.ok().body("Voto registrado com sucesso.");
        } catch (NotFoundException e) {
            log.info("Pauta não encontrada: "+ votoDTO.getPautaId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SessaoDeVotacaoEncerradaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (CpfJaVotouException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("Voto não pode ser computado para a pauta: "+ votoDTO.getPautaId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/{pautaId}/${votacaocontroller.buscarResultado}")
    public ResponseEntity<?> buscarResultado(@PathVariable String pautaId) {
        try {
            VotoResultadoResponse resultado = votoService.buscarResultado(pautaId);
            log.info("Resultado consultado, pauta: "+ pautaId);
            return ResponseEntity.ok().body(resultado);
        } catch (VotacaoPendenteException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (SessaoDeVotacaoEncerradaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (SessaoDeVotacaoNaoEncerradaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("Não foi possivel buscar resultado da pauta: "+ pautaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}