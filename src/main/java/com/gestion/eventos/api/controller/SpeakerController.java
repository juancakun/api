package com.gestion.eventos.api.controller;

import com.gestion.eventos.api.DTO.SpeakerRequestDto;
import com.gestion.eventos.api.DTO.SpeakerResponseDto;
import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.mapper.SpeakerMapper;
import com.gestion.eventos.api.service.SpeakerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Ponentes", description = "Operaciones para gestionar ponentes de eventos")
@RestController
@RequestMapping("/api/v1/speakers")
@RequiredArgsConstructor
public class SpeakerController {

    private final SpeakerService speakerService;
    private final SpeakerMapper speakerMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Crear un ponente",
            description = "Registra un nuevo ponente a partir de los datos recibidos. Los campos se validan antes de procesar la solicitud. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ponente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Los datos enviados no cumplen las validaciones"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para crear ponentes"),
            @ApiResponse(responseCode = "409", description = "Conflicto con la integridad de los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al crear el ponente")
    })
    public ResponseEntity<SpeakerResponseDto> createSpeaker(@Valid @RequestBody SpeakerRequestDto speakerDto){

        Speaker speaker = speakerService.save(speakerDto);
        return new ResponseEntity<>(speakerMapper.toDto(speaker), HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Obtener un ponente por ID",
            description = "Busca un ponente mediante su identificador único. Requiere el rol ADMIN o USER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponente encontrado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para consultar ponentes"),
            @ApiResponse(responseCode = "404", description = "No existe un ponente con el identificador proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno al consultar el ponente")
    })
    public ResponseEntity<SpeakerResponseDto> getSpeakerById(@PathVariable Long id){

        Speaker speakerOptional = speakerService.findById(id);
        return new ResponseEntity<>(speakerMapper.toDto(speakerOptional), HttpStatus.OK);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Obtener todos los ponentes",
            description = "Recupera la lista de todos los ponentes registrados en el sistema. Requiere el rol ADMIN o USER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponentes obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para consultar ponentes"),
            @ApiResponse(responseCode = "500", description = "Error interno al consultar los ponentes")
    })
    public ResponseEntity<List<SpeakerResponseDto>> getAllSpeaker(){
        List<Speaker> speakers = speakerService.findAll();
        return ResponseEntity.ok(speakerMapper.toResponseDtoList(speakers));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Actualizar un ponente",
            description = "Actualiza la información de un ponente existente mediante su identificador. Los datos enviados se validan antes de realizar la actualización. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponente actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Los datos enviados no cumplen las validaciones"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para actualizar ponentes"),
            @ApiResponse(responseCode = "404", description = "No existe un ponente con el identificador proporcionado"),
            @ApiResponse(responseCode = "409", description = "Conflicto con la integridad de los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar el ponente")
    })
    public ResponseEntity<SpeakerResponseDto> uptdateSpeaker(@PathVariable Long id,
                                                             @Valid @RequestBody SpeakerRequestDto speakerDto){
        Speaker updatedSpeaker = speakerService.update(id, speakerDto);
        return new ResponseEntity<>(speakerMapper.toDto(updatedSpeaker), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Eliminar un ponente",
            description = "Elimina un ponente mediante su identificador único. Si la operación tiene éxito, devuelve una respuesta sin contenido. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ponente eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para eliminar ponentes"),
            @ApiResponse(responseCode = "404", description = "No existe un ponente con el identificador proporcionado"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar el ponente debido a un conflicto de integridad de datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al eliminar el ponente")
    })
    public ResponseEntity<Void> deleteSpeaker(@PathVariable Long id){
        speakerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
