package com.gestion.eventos.api.security.dto;

import lombok.Data;

@Data
public class JwtAuthResponseDto {

    private String accesToken;
    private String tokenType = "Bearer";

    public JwtAuthResponseDto(String accesToken) {
        this.accesToken = accesToken;
    }

}