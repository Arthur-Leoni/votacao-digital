package com.example.votacaodigital.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "verifica-cpf", url = "${feign.url}")
public interface CpfApiClient {

    @GetMapping("${feign.endpoint}")
    boolean verificarCpf(@PathVariable("cpf") String cpf);

}
