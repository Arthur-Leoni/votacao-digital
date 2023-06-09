package com.example.votacaodigital.repository;

import com.example.votacaodigital.model.Voto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VotoRepository extends MongoRepository<Voto, Long> {
    List<Voto> findAllByPautaId(String pautaId);

    Optional<Voto> findByCpfAssociadoAndPautaId(String cpf, String pautaId);

}