package com.josec.catalog.service;

import com.josec.catalog.dto.UserRequestDTO;
import com.josec.catalog.dto.UserResponseDTO;
import com.josec.catalog.exception.EmailAlreadyExistsException;
import com.josec.catalog.exception.UsernameAlreadyExistsException;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; // Base de datos de usuarios

    // - MÉTODOS PRINCIPALES -

    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {

        // 1. Comprobar Username
        if(userRepository.existsByUsername(userRequestDTO.getUsername())) { //Comprobar si existe
          throw new UsernameAlreadyExistsException("Username already exists");

        }

        // 2. Comprobar Email
        if(userRepository.existsByEmail(userRequestDTO.getEmail())) { //Comprobar si existe el email
          throw new EmailAlreadyExistsException("The email is in use by another user");
        }

        // 3. Traducir, guardar y devolver
        User user = mapToEntity(userRequestDTO);// Traducir
        User savedUser = userRepository.save(user); // Guardar
        return mapToDTO(savedUser); // Devolver traducido a DTO

    }

    // - MÉTODOS PRIVADOS -

    /**
     * Mapea objetos @UserRequestDTO en objetos @User
     * @param userRequestDTO objeto DTO a convertir
     * @return Objeto User
     */
    private User mapToEntity(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        return user;
    }

    /**
     * Mapea objetos User en objetos @UserResponseDTO
     * @param user objeto usuario a convertir
     * @return objeto UserResponse convertido
     */
    private UserResponseDTO mapToDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setId(user.getId().longValue());
        return userResponseDTO;
    }
}
