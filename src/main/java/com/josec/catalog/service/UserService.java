package com.josec.catalog.service;

import com.josec.catalog.dto.*;
import com.josec.catalog.dto.mappers.UserMapper;
import com.josec.catalog.exception.*;
import com.josec.catalog.model.ReadList;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.ReadListRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import com.josec.catalog.util.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private PasswordEncoder passwordEncoder;


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

    // -- CAMBIOS DEL PERFIL DE USUARIO Y SEGURIDAD

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

    public String changePassword(ChangePasswordRequestDTO request){
        // 1. Comprobar usuario logeado
        Integer userId = permissionValidator.whoIsLoggedIn();
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with Id " + userId));

        // 2. Comprobar contraseña antigua
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Old password doesn't match");
        }
        // 3. Comprobar que no esté poniendo exactamente la misma contraseña
        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidPasswordException("New password cannot be the equal to the old password");
        }

        // 4. Encriptar nueva contraseña y guardar
        String newEncryptedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newEncryptedPassword);
        userRepository.save(user);

        return "La contraseña ha sido cambiada correctamente";
    }

    /**
     * Cambia la privacidad del perfil de público a privado y viceversa.
     *
     * @param privacy true o false
     */
    public void changePrivacy(boolean privacy){
        // 1. Comprobar usuario
        Integer userId = permissionValidator.whoIsLoggedIn();
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with Id " + userId));

        // 2. Cambiar privacidad
        System.out.println("New privacy: " + privacy + "Old privacy" + user.isPrivateProfile());
        if(privacy && user.isPrivateProfile()){
            throw new RuntimeException("Your profile is already private");
        }else if(!privacy && !user.isPrivateProfile()){
            throw new RuntimeException("Your profile is already public");
        }else{
            user.setPrivateProfile(privacy);
        }
        userRepository.save(user);
    }

    // --- BÚSQUEDA DE USUARIOS ---

    /**
     * Búsqueda de usuarios por nombre de usuario
     *
     * @param page núm. página
     * @param size tamaño de la página
     * @param query texto de búsqueda
     * @return Página de DTO de usuarios encontrados
     */
    @Transactional(readOnly = true)
    public Page<UserSummaryResponseDTO> searchUsers(String query, int page, int size){
        Integer userId = permissionValidator.whoIsLoggedIn();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("username").ascending());

        Page<User> result = userRepository.searchUsers(query, userId, pageRequest);

        return result.map(userMapper::toSummaryDTO);
    }

}
