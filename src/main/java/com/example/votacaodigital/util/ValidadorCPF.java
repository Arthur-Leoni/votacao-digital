package com.example.votacaodigital.util;

public class ValidadorCPF {

    public static boolean validarCPF(String cpf) {
        // Remover caracteres não numéricos
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verificar se possui 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verificar se todos os dígitos são iguais
        boolean todosDigitosIguais = true;
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != cpf.charAt(0)) {
                todosDigitosIguais = false;
                break;
            }
        }
        if (todosDigitosIguais) {
            return false;
        }

        // Verificar dígito verificador
        int[] digitos = new int[11];
        for (int i = 0; i < cpf.length(); i++) {
            digitos[i] = Integer.parseInt(cpf.substring(i, i + 1));
        }

        int soma1 = 0;
        int soma2 = 0;
        for (int i = 0; i < 9; i++) {
            soma1 += digitos[i] * (10 - i);
            soma2 += digitos[i] * (11 - i);
        }

        int resto1 = soma1 % 11;
        int digito1 = (resto1 < 2) ? 0 : 11 - resto1;

        soma2 += digito1 * 2;
        int resto2 = soma2 % 11;
        int digito2 = (resto2 < 2) ? 0 : 11 - resto2;

        return (digitos[9] == digito1 && digitos[10] == digito2);
    }
}
