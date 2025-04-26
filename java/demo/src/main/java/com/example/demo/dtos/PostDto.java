package com.example.demo.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private Integer commentCount;
}

@Data
class PostCreateRequest {
    private String userName;
    private String content;
}

@Data
class PostUpdateRequest {
    private String content;
}