package com.example.demo.models;

import lombok.Data;
import java.io.Serializable;

@Data
public class LikeId implements Serializable {
    private Integer post;
    private String userName;
}