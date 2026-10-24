package com.gestion.eventos.api.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Schema(description = "Detalles de la solicitud para crear/actualizar un evento")
public class EventRequestDto
{
    @Schema(description = "Nombre del evento", example = "Conferencia de Spring Boot")
    @NotBlank(message = "El nombre del evento no puede estar vacío.")
    private String name;
    @NotNull(message = "El evento tiene que tener una fecha.")
//    @PastOrPresent(message = "El evento no puede ser anterior a la fecha.")
    private LocalDate date;
    @NotBlank(message = "La localización no puede estar vacía.")
    private String location;

    @NotNull(message = "La categoria es obligatoria")
    private Long categoryId;
    @NotNull
    private Set<Long> speakerIds;

}
