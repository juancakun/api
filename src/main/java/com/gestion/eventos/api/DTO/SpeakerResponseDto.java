package com.gestion.eventos.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpeakerResponseDto {

    private Long id;
    private String name;
    private String email;
    private String bio;

}
