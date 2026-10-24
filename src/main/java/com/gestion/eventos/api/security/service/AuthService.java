package com.gestion.eventos.api.security.service;

import com.gestion.eventos.api.domain.Role;
import com.gestion.eventos.api.domain.User;
import com.gestion.eventos.api.exception.UserAlreadyExistsException;
import com.gestion.eventos.api.mapper.UserMapper;
import com.gestion.eventos.api.repository.RoleRepository;
import com.gestion.eventos.api.repository.UserRepository;
import com.gestion.eventos.api.security.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public void register(RegisterDto dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException(
                    "El nombre de usuario ya existe"
            );
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException(
                    "El correo electrónico ya existe"
            );
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el rol ROLE_USER en la base de datos"
                ));

        User user = userMapper.registerDtoToUser(dto);

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setRoles(new HashSet<>(Set.of(userRole)));

        userRepository.save(user);
    }
}