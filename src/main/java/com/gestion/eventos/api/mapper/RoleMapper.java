package com.gestion.eventos.api.mapper;

import com.gestion.eventos.api.DTO.RoleDto;
import com.gestion.eventos.api.domain.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toEntity(RoleDto roleDto);
    RoleDto toDto(Role role);
    List<RoleDto> toDtoList(List<Role> roles);

}
