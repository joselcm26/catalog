package com.josec.catalog.service;

import com.josec.catalog.dto.FollowConnectionResponseDTO;
import com.josec.catalog.dto.mappers.FollowConnectionMapper;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.FollowConnection;
import com.josec.catalog.model.User;
import com.josec.catalog.model.enums.FollowStatus;
import com.josec.catalog.repository.FollowConnectionRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar solicitudes de seguimiento
 */
@Service
public class FollowService {
    @Autowired
    private FollowConnectionRepository followRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionValidator permissionValidator;
    @Autowired
    private FollowConnectionMapper followConnectionMapper;

    /**
     * Enviar solicitud de seguimiento al usuario objetivo.
     * Si el perfil es privado, quedará como pendiente.
     *
     * @param targetUserID usuario objetivo
     */
    @Transactional
    public void followUser(Integer targetUserID) {

        // 1. Quien hace la solicitud
        Integer myId = permissionValidator.whoIsLoggedIn();

        if (myId.equals(targetUserID)) {
            throw new RuntimeException("You cannot follow yourself"); //TODO: excepción dedicada
        }

        // 2. Buscar los usuarios
        User me = userRepository.findById(myId.longValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + myId));
        User target = userRepository.findById(targetUserID.longValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserID));

        // 3. Comprobar si ya lo sigue, o si hay una solicitud pendiente
        if (followRepository.existsByFollowerIdAndFollowedId(myId, targetUserID)) {
            throw new RuntimeException("You already followed this user");
        }

        // 4. Iniciar seguimiento
        FollowConnection connection = new FollowConnection();
        connection.setFollower(me);
        connection.setFollowed(target);

        //Comprobar perfil privado
        if (target.isPrivateProfile()) {
            connection.setStatus(FollowStatus.PENDING);
        } else {
            connection.setStatus(FollowStatus.ACCEPTED);
        }

        // 5. Guardar
        followRepository.save(connection);
    }

    /**
     * Eliminar conexión de seguimiento con el usuario objetivo.
     *
     * @param followedId usuario actualmente seguido.
     */
    @Transactional
    public void unfollowUser(Integer followedId) {
        // 1. Quien está logeado
        Integer myId = permissionValidator.whoIsLoggedIn();

        // 2. Buscar conexion
        FollowConnection connection = followRepository.findByFollowerIdAndFollowedId(myId, followedId)
                .orElseThrow(() -> new RuntimeException("Connection not found with user: " + followedId));

        // 3. Eliminar
        followRepository.delete(connection);
    }

    /**
     * Aceptar una solicitud de seguimiento en estado pendiente.
     *
     * @param followerId Id del usuario que hace solicitud.
     */
    @Transactional
    public void acceptFollowRequest(Integer followerId) {
        // 1. Quien está logeado
        Integer myId = permissionValidator.whoIsLoggedIn();

        // 2. Buscar conexión donde el logeado es el seguido
        FollowConnection connection = followRepository.findByFollowerIdAndFollowedId(followerId, myId)
                .orElseThrow(() -> new RuntimeException("Follow request not found"));

        // 3. Comprobar si ya está aceptada
        if (connection.getStatus().equals(FollowStatus.ACCEPTED)) {
            throw new RuntimeException("You already accepted this request");
        }

        // 4. Aceptar
        connection.setStatus(FollowStatus.ACCEPTED);
        followRepository.save(connection);
    }

    /**
     * Rechazar una solicitud de seguimiento. La solicitud será eliminada.
     *
     * @param followerId Id del usuario que hace la solicitud
     */
    @Transactional
    public void rejectFollowRequest(Integer followerId) {
        // 1. Quien está logeado
        Integer myId = permissionValidator.whoIsLoggedIn();

        // 2. Buscar conexión donde el logeado es el seguido
        FollowConnection connection = followRepository.findByFollowerIdAndFollowedId(followerId, myId)
                .orElseThrow(() -> new RuntimeException("Follow request not found"));

        // 3. Comprobar si ya está aceptada
        if (!connection.getStatus().equals(FollowStatus.PENDING)) {
            throw new RuntimeException("You already accepted this request o rejected this request");
        }

        followRepository.delete(connection);

    }

    /**
     * Obtener lista de seguidores del usuario logeado
     *
     * @return List<FollowConnectionResponseDTO> followers
     */
    public List<FollowConnectionResponseDTO> getMyFollowers(){
        // 1. Quien está logeado
        Integer myId = permissionValidator.whoIsLoggedIn();

        // 2. Obtener lista de seguidores
        List<FollowConnection> followers = followRepository.findByFollowedIdAndStatus(myId, FollowStatus.ACCEPTED);

        // 3. Mapear

        return followers.stream()
                .map(followConnection -> followConnectionMapper.toDTO(followConnection)).toList();

    }

    /**
     * Obtener lista de usuarios seguidos por el usuario logeado
     *
     * @return List<FollowConnectionResponseDTO> followed
     */
    public List<FollowConnectionResponseDTO> getMyFollowedUsers(){
        // 1. Quien está logeado
        Integer myId = permissionValidator.whoIsLoggedIn();

        // 2. Obtener lista de seguidores
        List<FollowConnection> followers = followRepository.findByFollowerIdAndStatus(myId, FollowStatus.ACCEPTED);

        // 3. Mapear

        return followers.stream()
                .map(followConnection -> followConnectionMapper.toDTO(followConnection)).toList();

    }
}
