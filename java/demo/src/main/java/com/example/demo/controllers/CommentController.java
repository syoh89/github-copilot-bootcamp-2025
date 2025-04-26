package com.example.demo.controllers;

import com.example.demo.models.Comment;
import com.example.demo.services.CommentService;
import com.example.demo.dtos.CommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    
    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Integer postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@PathVariable Integer postId, @RequestBody CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setUserName(commentDto.getUserName());
        comment.setContent(commentDto.getContent());
        return new ResponseEntity<>(commentService.createComment(postId, comment), HttpStatus.CREATED);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable Integer postId, @PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.getComment(postId, commentId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.updateComment(postId, commentId, commentDto.getContent()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId) {
        try {
            logger.info("댓글 삭제 처리 시작 - 포스트ID: {}, 댓글ID: {}", postId, commentId);
            commentService.deleteComment(postId, commentId);
            logger.info("댓글 삭제 처리 완료 - 포스트ID: {}, 댓글ID: {}", postId, commentId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            logger.error("댓글 삭제 실패 - 해당 리소스 없음 - 포스트ID: {}, 댓글ID: {}, 오류: {}", 
                postId, commentId, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("댓글을 찾을 수 없습니다", e.getMessage()));
        } catch (Exception e) {
            logger.error("댓글 삭제 처리 중 오류 발생 - 포스트ID: {}, 댓글ID: {}, 오류: {}", 
                postId, commentId, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("댓글 삭제 중 오류가 발생했습니다", e.getMessage()));
        }
    }
    
    // 에러 응답을 위한 내부 클래스
    private static class ErrorResponse {
        private String message;
        private String detail;
        
        public ErrorResponse(String message, String detail) {
            this.message = message;
            this.detail = detail;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getDetail() {
            return detail;
        }
    }
}