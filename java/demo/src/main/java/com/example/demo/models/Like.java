package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
@Table(name = "likes")
@IdClass(LikeId.class)
public class Like {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    @JsonBackReference
    private Post post;

    @Id
    @Column(nullable = false)
    private String userName;
}