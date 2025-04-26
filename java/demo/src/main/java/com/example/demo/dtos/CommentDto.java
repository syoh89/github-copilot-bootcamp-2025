package com.example.demo.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long postId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Data
class CommentCreateRequest {
    private String userName;
    private String content;
}

@Data
class CommentUpdateRequest {
    private String content;
}