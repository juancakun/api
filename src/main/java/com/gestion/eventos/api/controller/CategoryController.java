package com.gestion.eventos.api.controller;

import com.gestion.eventos.api.DTO.CategoryDto;
import com.gestion.eventos.api.domain.Category;
import com.gestion.eventos.api.mapper.CategoryMapper;
import com.gestion.eventos.api.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Categorías", description = "Operaciones para gestionar las categorías de eventos")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Obtener todas las categorías",
            description = "Recupera la lista de todas las categorías registradas en el sistema. Requiere el rol ADMIN o USER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorías obtenidas exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para consultar categorías"),
            @ApiResponse(responseCode = "500", description = "Error interno al consultar las categorías")
    })
    public ResponseEntity<List<CategoryDto>> getAllCategories() {

        List<Category> categories = categoryService.findAll();

        return ResponseEntity.ok(categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Obtener una categoría por ID",
            description = "Busca una categoría mediante el parámetro id de la solicitud. Requiere el rol ADMIN o USER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "El parámetro id falta o tiene un formato inválido"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para consultar categorías"),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el identificador proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno al consultar la categoría")
    })
    public ResponseEntity<CategoryDto> getCategoryById(@RequestParam("id") Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Crear una categoría",
            description = "Registra una nueva categoría utilizando los datos recibidos en el cuerpo de la solicitud. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "La solicitud contiene datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para crear categorías"),
            @ApiResponse(responseCode = "409", description = "Conflicto con la integridad de los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al crear la categoría")
    })
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        category = categoryService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDto(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Actualizar una categoría",
            description = "Actualiza los datos de una categoría existente mediante su identificador. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "La solicitud contiene datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para actualizar categorías"),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el identificador proporcionado"),
            @ApiResponse(responseCode = "409", description = "Conflicto con la integridad de los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar la categoría")
    })
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        category = categoryService.update(id, category);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Eliminar una categoría",
            description = "Elimina una categoría mediante su identificador único. Si la operación tiene éxito, devuelve una respuesta sin contenido. Requiere el rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para eliminar categorías"),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el identificador proporcionado"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar la categoría debido a un conflicto de integridad de datos"),
            @ApiResponse(responseCode = "500", description = "Error interno al eliminar la categoría")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
