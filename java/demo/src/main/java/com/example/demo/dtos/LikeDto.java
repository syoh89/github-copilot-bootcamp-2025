package com.example.demo.dtos;

import lombok.Data;

@Data
public class LikeDto {
    private Integer postId;
    private String userName;
}

@Data
class LikeRequest {
    private String userName;
}