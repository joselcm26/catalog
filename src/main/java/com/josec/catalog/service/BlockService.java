package com.josec.catalog.service;

import com.josec.catalog.exception.AccessDeniedException;
import com.josec.catalog.exception.EntryAlreadyExistException;
import com.josec.catalog.exception.EntryNotFoundException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.BlockConnection;
import com.josec.catalog.model.FollowConnection;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.BlockConnectionRepository;
import com.josec.catalog.repository.FollowConnectionRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de bloqueo de contactos.
 *
 * El bloqueo es prioritario. Si A y B se siguen, y A bloquea a B, el servicio debe ir
 * a FollowConnectionRepository y borrar cualquier solicitud o seguimiento entre ellos.
 */
@Service
public class BlockService {

    // --- DEPENDENCIAS ---

    @Autowired
    private BlockConnectionRepository blockRepository;
    @Autowired
    private FollowConnectionRepository followRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionValidator permissionValidator;

    // --- METODOS ---

    /**
     * Métödo para bloquear usuarios. Borra las conexiones de seguimiento en ambos sentidos.
     *
     * @param targetUser usuario que va a ser bloqueado.
     */
    @Transactional
    public void blockUser(Integer targetUser){
        // Usuario que bloquea
        Integer myID = permissionValidator.whoIsLoggedIn();

        // Comprobaciones
        if(myID.equals(targetUser)){
            throw new AccessDeniedException("You cannot block yourself.");
        }

        if(blockRepository.existByBlockerIdAndBlockerId(myID.longValue(), targetUser.longValue())){
            throw new EntryAlreadyExistException("Yoy already have blocked this user.");
        }

        // Realizar bloqueo

        User me = userRepository.getReferenceById(myID.longValue());
        User target = userRepository.findById(targetUser.longValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUser));

        // - Destruir relaciones de seguimiento en ambas direcciones
        followRepository.deleteByFollowerIdAndFollowedId(myID, targetUser);
        followRepository.deleteByFollowerIdAndFollowedId(targetUser, myID);

        // - Crear bloqueo
        BlockConnection block = new BlockConnection();
        block.setBlocker(me);
        block.setBlocked(target);

        blockRepository.save(block);

    }

    /**
     * Permite a un usuario desbloquear a otro usuario que ya haya sido bloqueado previamente
     *
     * @param targetUser usuario que va a ser desbloqueado
     */
    @Transactional
    public void unblockUser(Integer targetUser){
        Integer myID = permissionValidator.whoIsLoggedIn();

        BlockConnection block = blockRepository.findByBlockerIdAndBlockedId(myID.longValue(), targetUser.longValue())
                .orElseThrow(() -> new EntryNotFoundException("You don't have this user blocked."));

        blockRepository.delete(block);
    }
}
