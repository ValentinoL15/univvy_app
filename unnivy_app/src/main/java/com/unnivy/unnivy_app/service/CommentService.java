package com.unnivy.unnivy_app.service;

import com.unnivy.unnivy_app.dto.CommentsDTOs.CommentDTO;
import com.unnivy.unnivy_app.dto.CommentsDTOs.CreateCommentDTO;
import com.unnivy.unnivy_app.dto.CommentsDTOs.EditCommentDTO;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.BusinessExceptionHandler;
import com.unnivy.unnivy_app.model.Client;
import com.unnivy.unnivy_app.model.Comments;
import com.unnivy.unnivy_app.model.Supplier;
import com.unnivy.unnivy_app.repository.IClientRepository;
import com.unnivy.unnivy_app.repository.ICommentsRepository;
import com.unnivy.unnivy_app.repository.ISupplierRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.ICommentService;
import com.unnivy.unnivy_app.service.ServicesInterfaces.ISupplierService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final ICommentsRepository commentsRepository;
    private final IClientRepository clientRepository;
    private final ISupplierRepository supplierRepository;

    @Override
    public List<CommentDTO> getAllComments() {
        List<Comments> listComments = commentsRepository.findAll();
        return listComments.stream().map(
                comment -> new CommentDTO(
                        comment.getComment_id(),
                        comment.getDescription(),
                        comment.getValue(),
                        comment.getFrom().getUser_id(),
                        comment.getFrom().getName(),
                        comment.getTo().getUser_id(),
                        comment.getTo().getName()
                )
        ).toList();
    }

    @Override
    public CommentDTO getComment(Long id_comment) {
        Comments comment = commentsRepository.findById(id_comment)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        CommentDTO commentDTO = new CommentDTO(comment.getComment_id(),comment.getDescription(),comment.getValue(),comment.getFrom().getUser_id(),comment.getFrom().getName(),
                comment.getTo().getUser_id(),comment.getTo().getName());
        return commentDTO;
    }

    @Override
    @Transactional
    public String createComment(Long fromId, Long toId,CreateCommentDTO commentDTO, String currentUser) {
        if(!clientRepository.existsById(fromId)){
            throw new EntityNotFoundException("El cliente con el id" + fromId + " no existe");
        }
        if (!supplierRepository.existsById(toId)) {
            throw new EntityNotFoundException("El proveedor con el id" + toId + " no existe");
        }
        if(commentsRepository.existsByFromIdAndToId(fromId,toId)){
            throw new BusinessExceptionHandler("Ya has dejado un comentario para este proveedor. No puedes duplicarlo.");
        }
        Client client = clientRepository.findClientEntityByUsername(currentUser)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Supplier supplier = supplierRepository.getReferenceById(toId);
        Comments comment = new Comments();
        comment.setDescription(commentDTO.description());
        comment.setValue(commentDTO.value());
        comment.setFrom(client);
        comment.setTo(supplier);
        commentsRepository.save(comment);
        return "Comentario creado con éxito";
    }

    @Override
    @Transactional
    public String editComment(Long id_comment, EditCommentDTO commentDTO, String currentUser) {
        Comments comment = commentsRepository.findById(id_comment)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        if(!comment.getFrom().getUsername().equals(currentUser)){
            throw new AccessDeniedException("No tienes permiso para editar este comentario.");
        }
        if(commentDTO.description() != null){
            comment.setDescription(commentDTO.description());
        }
        commentsRepository.save(comment);
        return "Comentario actualizado con éxito";
    }

    @Override
    @Transactional
    public void deleteComment(Long id_comment, String currentUser) {
        Comments comment = commentsRepository.findById(id_comment)
                .orElseThrow(() -> new EntityNotFoundException("Comentario no encontrado"));
        if (!commentsRepository.existsById(id_comment)) {
            throw new EntityNotFoundException("No se puede eliminar: el comentario con ID " + id_comment + " no existe.");
        }
        if(!comment.getFrom().getUsername().equals(currentUser)){
            throw new AccessDeniedException("No tienes permiso para borrar este comentario.");
        }

        commentsRepository.deleteById(id_comment);
    }
}
