package com.example.votacaodigital.repository;

import com.example.votacaodigital.model.Pauta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PautaRepository extends MongoRepository<Pauta, Long> {

    Optional<Pauta> findById(String pautaId);
}