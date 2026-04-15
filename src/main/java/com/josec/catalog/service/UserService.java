package com.josec.catalog.service;

import com.josec.catalog.dto.UserRequestDTO;
import com.josec.catalog.dto.UserResponseDTO;
import com.josec.catalog.dto.mappers.UserMapper;
import com.josec.catalog.exception.EmailAlreadyExistsException;
import com.josec.catalog.exception.UsernameAlreadyExistsException;
import com.josec.catalog.model.ReadList;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.ReadListRepository;
import com.josec.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // - DEPENDENCIAS -

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository; // Base de datos de usuarios

    @Autowired
    private ReadListRepository readListRepository;

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
        User user = userMapper.mapToEntity(userRequestDTO);// Traducir
        User savedUser = userRepository.save(user); // Guardar

        //Crear su readlist

        ReadList emptyReadList = new ReadList();
        emptyReadList.setOwner(savedUser);
        readListRepository.save(emptyReadList);

        return userMapper.mapToDTO(savedUser); // Devolver traducido a DTO

    }

}
