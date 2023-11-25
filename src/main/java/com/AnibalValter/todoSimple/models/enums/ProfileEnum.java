package com.AnibalValter.todoSimple.models.enums;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Anotação Lombok para gerar automaticamente um construtor que inclui todos os campos
@AllArgsConstructor
// Anotação Lombok para gerar automaticamente os métodos getter
@Getter
public enum ProfileEnum {
    // Enumerações para os perfis com códigos e descrições associadas
    ADMIN(1,"POLE_ADMIN"),
    USER(2, "ROLE_USER");
    
    // Código do perfil
    private Integer code;
    
    // Descrição do perfil
    private String description;

    // Método estático para converter um código em uma enumeração de perfil
    public static ProfileEnum toEnum(Integer code){

        // Retorna nulo se o código for nulo
        if(Objects.isNull(code))
            return null;

        // Itera sobre os valores da enumeração para encontrar a correspondência
        for(ProfileEnum x : ProfileEnum.values()){
            if(code.equals(x.getCode()))
                return x;
        }

        // Lança uma exceção se nenhum código correspondente for encontrado
        throw new IllegalArgumentException("Código inválido: " + code);
    }
}

