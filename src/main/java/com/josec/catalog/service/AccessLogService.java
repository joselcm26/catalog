package com.josec.catalog.service;

import com.josec.catalog.dto.AccessLogResponseDTO;
import com.josec.catalog.dto.mappers.AccessLogMapper;
import com.josec.catalog.exception.AccessDeniedException;
import com.josec.catalog.model.AccessLog;
import com.josec.catalog.model.User;
import com.josec.catalog.model.enums.Role;
import com.josec.catalog.repository.AccessLogRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio de registros de acceso
 */
@Service
public class AccessLogService {

    @Autowired
    private AccessLogRepository accessLogRepository;
    @Autowired
    private PermissionValidator permissionValidator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccessLogMapper accessLogMapper;


    /**
     * ### SOLO PARA ADMINISTRADORES ###
     *
     * Obtener página de registros de acceso
     *
     * @return página con los registros
     */
    public Page<AccessLogResponseDTO> getAllAccessLogs(int page, int size) {
        Integer userId = permissionValidator.whoIsLoggedIn();

        User user = userRepository.getReferenceById(userId.longValue());

        if(user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access Denied: Administrators only");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<AccessLog> logPage = accessLogRepository.findAll(pageable);

        return logPage
                .map( accessLog -> accessLogMapper.toDTO(accessLog));
    }

    /**
     * Obtener página de registros de un usuario concreto, útil para que
     * el usuario pueda ver sus accesos.
     *
     * @return página con registros
     */
    public Page<AccessLogResponseDTO> getMyAccessLogs(int page, int size) {
        Integer userId = permissionValidator.whoIsLoggedIn();

        Pageable pageable = PageRequest.of(page, size);
        Page<AccessLog> logPage = accessLogRepository.findByUserIdOrderByLoggedAtDesc(userId, pageable);

        return logPage
                .map( accessLog -> accessLogMapper.toDTO(accessLog));
    }
}
