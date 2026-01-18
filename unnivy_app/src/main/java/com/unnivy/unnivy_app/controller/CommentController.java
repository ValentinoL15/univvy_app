package com.unnivy.unnivy_app.controller;

import com.unnivy.unnivy_app.dto.CommentsDTOs.CommentDTO;
import com.unnivy.unnivy_app.dto.CommentsDTOs.CreateCommentDTO;
import com.unnivy.unnivy_app.dto.CommentsDTOs.EditCommentDTO;
import com.unnivy.unnivy_app.service.ServicesInterfaces.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CommentController {

    private final ICommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments(){
            return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/{id_comment}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long id_comment) {
        return ResponseEntity.ok(commentService.getComment(id_comment));
    }

    @PostMapping("/create/{from}/{to}")
    public ResponseEntity<String> createComment(@PathVariable Long from,
                                                @PathVariable Long to,
                                                @RequestBody CreateCommentDTO commentDTO,
                                                Principal principal){
        commentService.createComment(from,to,commentDTO,principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/editComment/{id_comment}")
    public ResponseEntity<String> editComment(@PathVariable Long id_comment,
                                              @RequestBody EditCommentDTO commentDTO,
                                              Principal principal){
        commentService.editComment(id_comment,commentDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/deleteComment/{id_comment}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id_comment,
                                              Principal principal){
        commentService.deleteComment(id_comment, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
