package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.UserRequestDTO;
import com.josec.catalog.dto.UserResponseDTO;
import com.josec.catalog.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Mapea objetos @UserRequestDTO en objetos @User
     * @param userRequestDTO objeto DTO a convertir
     * @return Objeto User
     */
    public User mapToEntity(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());

        // Encriptamos la contraseña antes de guardarla en el objeto User
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        return user;
    }

    /**
     * Mapea objetos User en objetos @UserResponseDTO
     * @param user objeto usuario a convertir
     * @return objeto UserResponse convertido
     */
    public UserResponseDTO mapToDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setId(user.getId().longValue());
        return userResponseDTO;
    }
}
