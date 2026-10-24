package com.gestion.eventos.api.mapper;

import com.gestion.eventos.api.DTO.UserResponseDto;
import com.gestion.eventos.api.exception.ResourceNotFoundException;
import com.gestion.eventos.api.security.dto.RegisterDto;
import com.gestion.eventos.api.domain.Role;
import com.gestion.eventos.api.domain.User;
import com.gestion.eventos.api.repository.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected RoleRepository roleRepository;

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "attendedEvents", ignore = true)
    public abstract User registerDtoToUser(RegisterDto dto);

    public abstract UserResponseDto toUserResponseDto(User user);

    public abstract List<UserResponseDto> toUserResponseDtoList(List<User> users);

    @Named("mapRoleStringsToRoles")
    public Set<Role> mapRoleStringsToRoles(Set<String> roles) {

        if(roles == null || roles.isEmpty() ) {
            return roleRepository.findByName("ROLE_USER")
                    .map(Collections::singleton)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Rol 'ROLE_USER' no encontrado en la base de datos. " +
                            "Asegúrate de que el rol ROLE_USER existe en la base de datos."));
        }

        return roles.stream()
                .map(
                        role -> roleRepository.findByName(role)
                                .orElseThrow(
                                        () -> new ResourceNotFoundException("Error: Rol no encontrado: " + role)
                                ))
                .collect(Collectors.toSet());
    }

}
