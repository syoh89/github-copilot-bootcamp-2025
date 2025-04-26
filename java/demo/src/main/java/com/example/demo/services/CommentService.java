package com.example.demo.services;

import com.example.demo.models.Comment;
import com.example.demo.models.Post;
import com.example.demo.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@Transactional
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostService postService;

    public List<Comment> getCommentsByPostId(Integer postId) {
        try {
            logger.info("포스트 댓글 조회 시작 - postId: {}", postId);
            List<Comment> comments = commentRepository.findByPostId(postId);
            logger.info("포스트 댓글 조회 완료 - postId: {}, 댓글 수: {}", postId, comments.size());
            return comments;
        } catch (Exception e) {
            logger.error("포스트 댓글 조회 중 오류 발생 - postId: {}, 오류: {}", postId, e.getMessage(), e);
            throw e;
        }
    }

    public Comment getComment(Integer postId, Integer commentId) {
        try {
            logger.info("댓글 조회 시작 - postId: {}, commentId: {}", postId, commentId);
            
            Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("댓글을 찾을 수 없음 - commentId: {}", commentId);
                    return new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId);
                });
            
            if (!comment.getPost().getId().equals(postId)) {
                logger.error("댓글이 해당 포스트에 속하지 않음 - postId: {}, commentId: {}", postId, commentId);
                throw new EntityNotFoundException("해당 포스트에 속한 댓글을 찾을 수 없습니다.");
            }
            
            logger.info("댓글 조회 완료 - commentId: {}", commentId);
            return comment;
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 위에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("댓글 조회 중 오류 발생 - postId: {}, commentId: {}, 오류: {}",
                postId, commentId, e.getMessage(), e);
            throw e;
        }
    }

    public Comment createComment(Integer postId, Comment comment) {
        try {
            logger.info("댓글 생성 시작 - postId: {}, userName: {}", postId, comment.getUserName());
            
            Post post = postService.getPost(postId);
            if (post == null) {
                logger.error("댓글 생성 실패 - 포스트를 찾을 수 없음 - postId: {}", postId);
                throw new EntityNotFoundException("포스트를 찾을 수 없습니다. ID: " + postId);
            }
            
            comment.setPost(post);
            Comment savedComment = commentRepository.save(comment);
            postService.incrementCommentCount(postId);
            
            logger.info("댓글 생성 완료 - postId: {}, commentId: {}", postId, savedComment.getId());
            return savedComment;
        } catch (Exception e) {
            logger.error("댓글 생성 중 오류 발생 - postId: {}, 오류: {}", postId, e.getMessage(), e);
            throw e;
        }
    }

    public Comment updateComment(Integer postId, Integer commentId, String content) {
        try {
            logger.info("댓글 수정 시작 - postId: {}, commentId: {}", postId, commentId);
            
            Comment comment = getComment(postId, commentId);
            comment.setContent(content);
            Comment updatedComment = commentRepository.save(comment);
            
            logger.info("댓글 수정 완료 - commentId: {}", commentId);
            return updatedComment;
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 getComment 메서드에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("댓글 수정 중 오류 발생 - postId: {}, commentId: {}, 오류: {}",
                postId, commentId, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteComment(Integer postId, Integer commentId) {
        try {
            logger.info("댓글 삭제 시작 - postId: {}, commentId: {}", postId, commentId);
            
            Comment comment = getComment(postId, commentId);
            commentRepository.delete(comment);
            postService.decrementCommentCount(postId);
            
            logger.info("댓글 삭제 완료 - postId: {}, commentId: {}", postId, commentId);
        } catch (EntityNotFoundException e) {
            // 로깅은 이미 getComment 메서드에서 수행됨
            throw e;
        } catch (Exception e) {
            logger.error("댓글 삭제 중 오류 발생 - postId: {}, commentId: {}, 오류: {}",
                postId, commentId, e.getMessage(), e);
            throw e;
        }
    }
}