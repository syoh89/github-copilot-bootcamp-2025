package com.example.demo.controllers;

import com.example.demo.models.Post;
import com.example.demo.services.PostService;
import com.example.demo.dtos.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostDto postDto) {
        Post post = new Post();
        post.setUserName(postDto.getUserName());
        post.setContent(postDto.getContent());
        return new ResponseEntity<>(postService.createPost(post), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPost(@PathVariable Integer id) {
        try {
            logger.info("포스트 조회 시작 - ID: {}", id);
            Post post = postService.getPost(id);
            logger.info("포스트 조회 완료 - ID: {}", id);
            return ResponseEntity.ok(post);
        } catch (EntityNotFoundException e) {
            logger.error("포스트 조회 실패 - 해당 리소스 없음 - ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("포스트를 찾을 수 없습니다", e.getMessage()));
        } catch (Exception e) {
            logger.error("포스트 조회 중 오류 발생 - ID: {}, 오류: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("포스트 조회 중 오류가 발생했습니다", e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updatePost(@PathVariable Integer id, @RequestBody PostDto postDto) {
        try {
            logger.info("포스트 수정 시작 - ID: {}", id);
            Post post = postService.updatePost(id, postDto.getContent());
            logger.info("포스트 수정 완료 - ID: {}", id);
            return ResponseEntity.ok(post);
        } catch (EntityNotFoundException e) {
            logger.error("포스트 수정 실패 - 해당 리소스 없음 - ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("포스트를 찾을 수 없습니다", e.getMessage()));
        } catch (Exception e) {
            logger.error("포스트 수정 중 오류 발생 - ID: {}, 오류: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("포스트 수정 중 오류가 발생했습니다", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePost(@PathVariable Integer id) {
        try {
            logger.info("포스트 삭제 시작 - ID: {}", id);
            postService.deletePost(id);
            logger.info("포스트 삭제 완료 - ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            logger.error("포스트 삭제 실패 - 해당 리소스 없음 - ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("포스트를 찾을 수 없습니다", e.getMessage()));
        } catch (Exception e) {
            logger.error("포스트 삭제 중 오류 발생 - ID: {}, 오류: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("포스트 삭제 중 오류가 발생했습니다", e.getMessage()));
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