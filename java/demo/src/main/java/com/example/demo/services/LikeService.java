package com.example.demo.services;

import com.example.demo.models.Like;
import com.example.demo.models.Post;
import com.example.demo.repositories.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class LikeService {
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private PostService postService;

    public void likePost(Integer postId, String userName) {
        try {
            logger.info("좋아요 등록 시작 - postId: {}, userName: {}", postId, userName);
            
            if (!likeRepository.existsByPostIdAndUserName(postId, userName)) {
                Post post = postService.getPost(postId);
                if (post == null) {
                    logger.error("좋아요 실패 - 포스트를 찾을 수 없음 - postId: {}", postId);
                    throw new EntityNotFoundException("포스트를 찾을 수 없습니다. ID: " + postId);
                }
                
                Like like = new Like();
                like.setPost(post);
                like.setUserName(userName);
                
                likeRepository.save(like);
                postService.incrementLikeCount(postId);
                
                logger.info("좋아요 등록 성공 - postId: {}, userName: {}", postId, userName);
            } else {
                logger.info("좋아요 이미 존재함 - postId: {}, userName: {}", postId, userName);
            }
        } catch (Exception e) {
            logger.error("좋아요 등록 중 오류 발생 - postId: {}, userName: {}, 오류: {}", 
                postId, userName, e.getMessage(), e);
            throw e;
        }
    }

    public void unlikePost(Integer postId, String userName) {
        try {
            logger.info("좋아요 취소 시작 - postId: {}, userName: {}", postId, userName);
            
            if (likeRepository.existsByPostIdAndUserName(postId, userName)) {
                likeRepository.deleteByPostIdAndUserName(postId, userName);
                postService.decrementLikeCount(postId);
                
                logger.info("좋아요 취소 성공 - postId: {}, userName: {}", postId, userName);
            } else {
                logger.info("좋아요가 존재하지 않음 - postId: {}, userName: {}", postId, userName);
            }
        } catch (Exception e) {
            logger.error("좋아요 취소 중 오류 발생 - postId: {}, userName: {}, 오류: {}", 
                postId, userName, e.getMessage(), e);
            throw e;
        }
    }
}