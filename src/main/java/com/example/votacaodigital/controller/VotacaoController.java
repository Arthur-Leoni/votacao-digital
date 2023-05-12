package com.example.votacaodigital.controller;

import com.example.votacaodigital.exceptions.CpfInvalidoException;
import com.example.votacaodigital.exceptions.CpfJaVotouException;
import com.example.votacaodigital.exceptions.NotFoundException;
import com.example.votacaodigital.exceptions.SessaoDeVotacaoEncerradaException;
import com.example.votacaodigital.exceptions.SessaoDeVotacaoJaIniciadaException;
import com.example.votacaodigital.exceptions.SessaoDeVotacaoNaoEncerradaException;
import com.example.votacaodigital.exceptions.VotacaoPendenteException;
import com.example.votacaodigital.model.CreatePautaDTO;
import com.example.votacaodigital.model.Pauta;
import com.example.votacaodigital.model.Voto;
import com.example.votacaodigital.model.VotoDTO;
import com.example.votacaodigital.model.VotoResultadoResponse;
import com.example.votacaodigital.service.PautaService;
import com.example.votacaodigital.service.VotoService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${votacaocontroller.mapping}")
@Log4j2
public class VotacaoController {

    @Autowired
    private PautaService pautaService;

    @Autowired
    private VotoService votoService;
    @ApiOperation("Cria uma nova pauta")
    @PostMapping
    public ResponseEntity<Pauta> criarPauta(@RequestBody CreatePautaDTO createPautaRequest) {
        Pauta novaPauta = pautaService.save(createPautaRequest.toDomain());
        log.info("Nova pauta criada: "+ novaPauta.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPauta);
    }
    @ApiOperation("Inicia votação em uma existente")
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
    @ApiOperation("Registra voto em uma pauta com sessao aberta")
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
        } catch (CpfInvalidoException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("Voto não pode ser computado para a pauta: "+ votoDTO.getPautaId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    @ApiOperation("Busca resultado de sessão ja finalizada")
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

    @GetMapping("/verificacpf/{cpf}")
    public boolean verificarCpf(@PathVariable String cpf) {
        //Apenas uma simulação como se fosse utilizar um endpoit externo

        //logica simples cpfs terminados em 0 não serão aceitos (apenas para teste)
        if((cpf.charAt(cpf.length() - 1)) == '0') {
            return false;
        }
        return true;
    }

}