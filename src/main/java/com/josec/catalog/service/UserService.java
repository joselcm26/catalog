package com.josec.catalog.service;

import com.josec.catalog.dto.UserRequestDTO;
import com.josec.catalog.dto.UserResponseDTO;
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

        if(userRequestDTO != null) {
          User user = mapToEntity(userRequestDTO);// Traducir
          if(userRepository.findByUsername(user.getUsername()).isPresent()) { //Comprobar si existe
              throw new RuntimeException("Username already exist");
          }
          User savedUser = userRepository.save(user); // Guardar
          return mapToDTO(savedUser); // Devolver traducido a DTO
        }
        throw new RuntimeException("Data provided is invalid");
    }

    // - MÉTODOS PRIVADOS -

    /**
     * Mapea objetos @UserRequestDTO en objetos @User
     * @param userRequestDTO
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
        return userResponseDTO;
    }
}
