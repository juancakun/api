package com.gestion.eventos.api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpeakerRequestDto {


    @NotBlank(message = "El nomre del ponente no puede estar vacío.")
    @Size(max = 100, message = "El nomre del ponente no puede exceder los 100 caracteres.")
    private String name;

    @NotBlank(message ="El email del ponente no puede estar vacío.")
    @Email(message ="El correo del ponente no tiene el formato correcto.")
    @Size(max = 100, message = "El email del ponente no puede exceder los 100 caracteres.")
    private String email;

    @Size(max = 500, message = "La biografía no puede exceder los 500 caracteres.")
    private String bio;

}
