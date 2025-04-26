package com.example.demo.services;

import com.example.demo.models.Post;
import com.example.demo.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@Transactional
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    
    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        try {
            logger.info("모든 포스트 조회 시작");
            List<Post> posts = postRepository.findAll();
            logger.info("모든 포스트 조회 완료 - 포스트 수: {}", posts.size());
            return posts;
        } catch (Exception e) {
            logger.error("모든 포스트 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Post getPost(Integer id) {
        try {
            logger.info("포스트 조회 시작 - id: {}", id);
            
            Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("포스트를 찾을 수 없음 - id: {}", id);
                    return new EntityNotFoundException("포스트를 찾을 수 없습니다. ID: " + id);
                });
                
            logger.info("포스트 조회 완료 - id: {}", id);
            return post;
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 위에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("포스트 조회 중 오류 발생 - id: {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public Post createPost(Post post) {
        try {
            logger.info("포스트 생성 시작 - userName: {}", post.getUserName());
            
            post.setLikeCount(0);
            post.setCommentCount(0);
            Post savedPost = postRepository.save(post);
            
            logger.info("포스트 생성 완료 - id: {}", savedPost.getId());
            return savedPost;
        } catch (Exception e) {
            logger.error("포스트 생성 중 오류 발생 - userName: {}, 오류: {}", 
                post.getUserName(), e.getMessage(), e);
            throw e;
        }
    }

    public Post updatePost(Integer id, String content) {
        try {
            logger.info("포스트 수정 시작 - id: {}", id);
            
            Post post = getPost(id);
            post.setContent(content);
            Post updatedPost = postRepository.save(post);
            
            logger.info("포스트 수정 완료 - id: {}", id);
            return updatedPost;
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 getPost 메서드에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("포스트 수정 중 오류 발생 - id: {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void deletePost(Integer id) {
        try {
            logger.info("포스트 삭제 시작 - id: {}", id);
            
            Post post = getPost(id);
            postRepository.delete(post);
            
            logger.info("포스트 삭제 완료 - id: {}", id);
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 getPost 메서드에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("포스트 삭제 중 오류 발생 - id: {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void incrementLikeCount(Integer id) {
        try {
            logger.info("좋아요 수 증가 시작 - postId: {}", id);
            
            Post post = getPost(id);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            
            logger.info("좋아요 수 증가 완료 - postId: {}, 새 좋아요 수: {}", id, post.getLikeCount());
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 getPost 메서드에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("좋아요 수 증가 중 오류 발생 - postId: {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void decrementLikeCount(Integer id) {
        try {
            logger.info("좋아요 수 감소 시작 - postId: {}", id);
            
            Post post = getPost(id);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            
            logger.info("좋아요 수 감소 완료 - postId: {}, 새 좋아요 수: {}", id, post.getLikeCount());
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 getPost 메서드에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("좋아요 수 감소 중 오류 발생 - postId: {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void incrementCommentCount(Integer id) {
        try {
            logger.info("댓글 수 증가 시작 - postId: {}", id);
            
            Post post = getPost(id);
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);
            
            logger.info("댓글 수 증가 완료 - postId: {}, 새 댓글 수: {}", id, post.getCommentCount());
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 getPost 메서드에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("댓글 수 증가 중 오류 발생 - postId: {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void decrementCommentCount(Integer id) {
        try {
            logger.info("댓글 수 감소 시작 - postId: {}", id);
            
            Post post = getPost(id);
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postRepository.save(post);
            
            logger.info("댓글 수 감소 완료 - postId: {}, 새 댓글 수: {}", id, post.getCommentCount());
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 getPost 메서드에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("댓글 수 감소 중 오류 발생 - postId: {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}