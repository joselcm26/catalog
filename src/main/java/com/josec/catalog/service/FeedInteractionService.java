package com.josec.catalog.service;

import com.josec.catalog.dto.CommentRequestDTO;
import com.josec.catalog.dto.CommentResponseDTO;
import com.josec.catalog.dto.MediaLogResponseDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.dto.mappers.CommentMapper;
import com.josec.catalog.dto.mappers.MediaLogMapper;
import com.josec.catalog.exception.AccessDeniedException;
import com.josec.catalog.exception.EmptyReadingLogException;
import com.josec.catalog.exception.MediaLogNotFoundException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.*;
import com.josec.catalog.repository.CommentRepository;
import com.josec.catalog.repository.MediaLogLikeRepository;
import com.josec.catalog.repository.MediaLogRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio para interacciones con el feed
 */
@Service
public class FeedInteractionService {

    @Autowired
    private MediaLogLikeRepository likeRepository;
    @Autowired
    private MediaLogRepository mediaLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionValidator permissionValidator;
    @Autowired
    private MediaLogMapper  mediaLogMapper;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentMapper commentMapper;

    // -- FEEDS --

    public Page<MediaLogResponseDTO> getMyDiary(int page, int size) {
        // 1. Extraer el Id de usuario
        Integer userId = permissionValidator.whoIsLoggedIn();

        // 2. Obtener su lista de logs y devolver
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<MediaLog> logPage = mediaLogRepository.findMyDiary(userId, pageable);

        if(!logPage.isEmpty()) {
            return logPage.map(mediaLogMapper::toDTO); // Mapear
        }  else {
            throw new EmptyReadingLogException("The read log for this user is empty");
        }
    }

    public Page<MediaLogResponseDTO> getMyHomeFeed(int page, int size) {
        // 1. Extraer el Id de usuario
        Integer userId = permissionValidator.whoIsLoggedIn();

        // 2. Obtener su lista de logs y devolver
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<MediaLog> logPage = mediaLogRepository.findHomeFeed(userId, pageable);

        //TODO: rellenar likes y si el usuario ya tiene like


        if(!logPage.isEmpty()) {
            return logPage.map(mediaLogMapper::toDTO); // Mapear
        }  else {
            throw new EmptyReadingLogException("The home feed for this user is empty");
        }
    }

    public Page<MediaLogResponseDTO> getExploreFeed(int page, int size) {

        // Obtener su lista de logs y devolver
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<MediaLog> logPage = mediaLogRepository.findExploreFeed(pageable);

        //TODO: rellenar likes y si el usuario ya tiene like


        if(!logPage.isEmpty()) {
            return logPage.map(mediaLogMapper::toDTO); // Mapear
        }  else {
            throw new EmptyReadingLogException("The explore feed is empty");
        }
    }

    // --- LIKES ---

    @Transactional
    public void toggleLike (Integer mediaLogId){
        // 1. Usuario
        Integer userId = permissionValidator.whoIsLoggedIn();

        // 2. Comprobar que el post existe
        MediaLog log = mediaLogRepository.findById(mediaLogId.longValue())
                .orElseThrow(() -> new MediaLogNotFoundException("Media log not found with id: " + mediaLogId));

        // 3. Comprobar si el usuario ya le dio like
        Optional<MediaLogLike> existingLike = likeRepository.findByUserIdAndMediaLogId(userId, mediaLogId);

        if (existingLike.isPresent()) {
            // Si ya existe se borra
            likeRepository.delete(existingLike.get());
        }else{
            // Si no existe se crea
            User user = userRepository.getReferenceById(userId.longValue());
            MediaLogLike newLike = new MediaLogLike();
            newLike.setUser(user);
            newLike.setMediaLog(log);
            likeRepository.save(newLike);
        }
    }

    // --- COMENTARIOS ---

    /**
     * Añade un comentario al MediaLog con los datos proporcionados
     *
     * @param mediaLogId al que se va añadir
     * @param commentRequestDTO contenido
     * @return respuesta con el comentario generado
     */
    @Transactional
    public CommentResponseDTO addComment(Integer mediaLogId, CommentRequestDTO commentRequestDTO) {
        //1. Identificar usuario
        Integer userId = permissionValidator.whoIsLoggedIn();

        //2. buscar log
        MediaLog log = mediaLogRepository.findById(mediaLogId.longValue())
                .orElseThrow(() -> new MediaLogNotFoundException("Media log not found with id: " + mediaLogId));

        //3. Recuperar usuario
        User author = userRepository.getReferenceById(userId.longValue());

        Comment comment = new Comment();
        comment.setContent(commentRequestDTO.getContent());
        comment.setAuthor(author);
        comment.setMediaLog(log);

        //4. Guardar y devolver
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    /**
     * Borra un comentario si el que lo hace es autor del comentario o propietario del post.
     * @param commentId
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Integer userId = permissionValidator.whoIsLoggedIn();

        //Recuperar comentario
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        //Comprobar autor o propietario del medialog
        boolean isAuthor = comment.getAuthor().getId().equals(userId);
        boolean isPostOwner = comment.getAuthor().getId().equals(userId);

        if (!isAuthor && !isPostOwner) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
