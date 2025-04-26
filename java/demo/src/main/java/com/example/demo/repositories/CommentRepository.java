package com.example.demo.repositories;

import com.example.demo.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findByPostId(@Param("postId") Integer postId);
}