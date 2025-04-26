package com.example.demo.controllers;

import com.example.demo.services.LikeService;
import com.example.demo.dtos.LikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@CrossOrigin
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);
    
    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<Object> likePost(@PathVariable Integer postId, @RequestBody LikeDto likeDto) {
        try {
            logger.info("좋아요 처리 시작 - 포스트ID: {}, 사용자: {}", postId, likeDto.getUserName());
            likeService.likePost(postId, likeDto.getUserName());
            logger.info("좋아요 처리 완료 - 포스트ID: {}, 사용자: {}", postId, likeDto.getUserName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("좋아요 처리 중 오류 발생 - 포스트ID: {}, 사용자: {}, 오류: {}", 
                postId, likeDto.getUserName(), e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("좋아요 처리 중 오류가 발생했습니다", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> unlikePost(
        @PathVariable Integer postId, 
        @RequestParam(name = "userName") String userName) {
        try {
            logger.info("좋아요 취소 처리 시작 - 포스트ID: {}, 사용자: {}", postId, userName);
            likeService.unlikePost(postId, userName);
            logger.info("좋아요 취소 처리 완료 - 포스트ID: {}, 사용자: {}", postId, userName);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("좋아요 취소 처리 중 오류 발생 - 포스트ID: {}, 사용자: {}, 오류: {}", 
                postId, userName, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("좋아요 취소 처리 중 오류가 발생했습니다", e.getMessage()));
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