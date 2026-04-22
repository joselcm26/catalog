package com.josec.catalog.service;

import com.josec.catalog.dto.UserProfileUpdateRequestDTO;
import com.josec.catalog.dto.UserRequestDTO;
import com.josec.catalog.dto.UserResponseDTO;
import com.josec.catalog.dto.mappers.UserMapper;
import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.exception.EmailAlreadyExistsException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.exception.UsernameAlreadyExistsException;
import com.josec.catalog.model.ReadList;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.ReadListRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import com.josec.catalog.util.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PermissionValidator permissionValidator;


    // - MÉTODOS PRINCIPALES -

    public UserResponseDTO getMyUserInfo(){

        // 1. Quién está loggeado
        Integer loggedUserId = permissionValidator.whoIsLoggedIn();

        // 2. Buscar los datos
        User user = userRepository.findById(loggedUserId.longValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + loggedUserId));

        // 3. Mapear y devolver

        return userMapper.toDTO(user);

    }

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
        User user = userMapper.toEntity(userRequestDTO);// Traducir
        User savedUser = userRepository.save(user); // Guardar

        //Crear su readlist

        ReadList emptyReadList = new ReadList();
        emptyReadList.setOwner(savedUser);
        readListRepository.save(emptyReadList);

        return userMapper.toDTO(savedUser); // Devolver traducido a DTO

    }

    public UserResponseDTO updateUser(Integer userId, UserProfileUpdateRequestDTO requestDTO) {

        // 1. Obtener entidad usuario
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with Id: " + userId));

        // 2. Modificar los datos

        userMapper.updateEntityFromDto(requestDTO, user);

        // 3. Guardar, mapear y devolver
        userRepository.save(user);
        return userMapper.toDTO(user);

    }

    public UserResponseDTO updateCoverImage(int id, String filename){
        User user = userRepository.findById((long)id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        user.setProfileImage(filename);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

}
