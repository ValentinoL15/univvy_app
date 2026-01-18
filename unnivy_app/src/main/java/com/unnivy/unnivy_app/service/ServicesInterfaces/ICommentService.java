package com.unnivy.unnivy_app.service.ServicesInterfaces;

import com.unnivy.unnivy_app.dto.CommentsDTOs.CommentDTO;
import com.unnivy.unnivy_app.dto.CommentsDTOs.CreateCommentDTO;
import com.unnivy.unnivy_app.dto.CommentsDTOs.EditCommentDTO;

import java.util.List;

public interface ICommentService {

    public List<CommentDTO> getAllComments();

    public CommentDTO getComment(Long id_comment);

    public String createComment(Long from, Long to,CreateCommentDTO commentDTO, String currentUser);

    public String editComment(Long id_comment, EditCommentDTO commentDTO, String currentUser);

    public void deleteComment(Long id_comment, String currentUser);

}
