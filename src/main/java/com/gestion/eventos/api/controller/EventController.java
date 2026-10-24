package com.gestion.eventos.api.controller;

import com.gestion.eventos.api.DTO.EventRequestDto;
import com.gestion.eventos.api.DTO.EventResponseDto;
import com.gestion.eventos.api.domain.Event;
import com.gestion.eventos.api.mapper.EventMapper;
import com.gestion.eventos.api.service.IEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Eventos", description = "Operaciones relacionadas con la gestión de eventos")
@RestController

@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;
    private final EventMapper eventMapper;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @GetMapping("/problematic")
    @Operation(
            summary = "Obtener eventos con detalles (consulta no optimizada)",
            description = "Obtiene todos los eventos y sus detalles mediante la implementación no optimizada. Este endpoint permite analizar el problema N+1 de consultas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos obtenidos exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al consultar los eventos")
    })
    public ResponseEntity<List<Event>> getAllEventsProblematic(){
        List<Event> events = eventService.getAllEventsAndTheirDetailsProblematic();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/optimized")
    @Operation(
            summary = "Obtener eventos con detalles (consulta optimizada)",
            description = "Obtiene todos los eventos y sus detalles utilizando JOIN FETCH para optimizar la carga de las relaciones y reducir consultas adicionales."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos obtenidos exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al consultar los eventos")
    })
    public ResponseEntity<List<Event>> getAllEventsOptimizedWithJoinFetch(){
        List<Event> events = eventService.getAllEventsAndTheirDetailsOptimizedWithJoinFetch();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Obtener todos los eventos",
            description = "Obtiene los eventos de forma paginada. Permite filtrar por nombre y configurar la página, el tamaño y el ordenamiento. Requiere el rol ADMIN o USER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para consultar eventos"),
            @ApiResponse(responseCode = "500", description = "Error interno al consultar los eventos")
    })
    public ResponseEntity<Page<EventResponseDto>> getAllEvents(
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {

        Page<EventResponseDto> events = eventService.findAll(name, pageable);
        return new ResponseEntity<>(events, HttpStatus.OK);

    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Crear un evento",
            description = "Registra un nuevo evento a partir de los datos recibidos. Los campos se validan antes de procesar la solicitud. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Los datos enviados no cumplen las validaciones"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para crear eventos"),
            @ApiResponse(responseCode = "409", description = "Conflicto con la integridad de los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al crear el evento")
    })
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto requestDto){
        logger.info("Recibida solicitud para crear evento: {}", requestDto.getName());

        Event eventSaved = eventService.save(requestDto);

        EventResponseDto responseDto = eventMapper.toResponseDto(eventSaved);

        logger.debug("Evento creado exitosamente: {}", eventSaved.getName());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Obtener un evento por ID",
            description = "Busca un evento mediante su identificador único y devuelve sus datos de respuesta. Requiere el rol ADMIN o USER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para consultar eventos"),
            @ApiResponse(responseCode = "404", description = "No existe un evento con el identificador proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno al consultar el evento")
    })
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id){
        Event event = eventService.findById(id);
        EventResponseDto responseDto = eventMapper.toResponseDto(event);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Actualizar un evento",
            description = "Actualiza los datos de un evento existente mediante su identificador. Los datos enviados se validan antes de realizar la actualización. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Los datos enviados no cumplen las validaciones"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para actualizar eventos"),
            @ApiResponse(responseCode = "404", description = "No existe un evento con el identificador proporcionado"),
            @ApiResponse(responseCode = "409", description = "Conflicto con la integridad de los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar el evento")
    })
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDto requestDto){

        Event updateEvent = eventService.update(id,  requestDto);

        return ResponseEntity.ok(eventMapper.toResponseDto(updateEvent));

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Eliminar un evento",
            description = "Elimina un evento mediante su identificador único. Si la operación tiene éxito, devuelve una respuesta sin contenido. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para eliminar eventos"),
            @ApiResponse(responseCode = "404", description = "No existe un evento con el identificador proporcionado"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar el evento debido a un conflicto de integridad de datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al eliminar el evento")
    })
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id){
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}


