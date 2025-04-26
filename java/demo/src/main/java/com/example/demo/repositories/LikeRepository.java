package com.example.demo.repositories;

import com.example.demo.models.Like;
import com.example.demo.models.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, LikeId> {
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.post.id = :postId AND l.userName = :userName")
    boolean existsByPostIdAndUserName(@Param("postId") Integer postId, @Param("userName") String userName);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Like l WHERE l.post.id = :postId AND l.userName = :userName")
    void deleteByPostIdAndUserName(@Param("postId") Integer postId, @Param("userName") String userName);
}