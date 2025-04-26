import React, { useState, useEffect } from 'react';
import Button from './Button';
import { getComments, createComment, deleteComment } from '../services/api';

const CommentSection = ({ postId, userName }) => {
  const [comments, setComments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [newComment, setNewComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  
  // 댓글 목록 불러오기
  const fetchComments = async () => {
    setIsLoading(true);
    setError('');
    
    try {
      const fetchedComments = await getComments(postId);
      setComments(fetchedComments);
    } catch (err) {
      console.error('댓글 불러오기 오류:', err);
      setError('댓글을 불러오는데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };
  
  // 컴포넌트 마운트 시 댓글 불러오기
  useEffect(() => {
    fetchComments();
  }, [postId]);
  
  // 새 댓글 작성 처리
  const handleSubmitComment = async (e) => {
    e.preventDefault();
    
    if (!newComment.trim()) return;
    
    setIsSubmitting(true);
    setError('');
    
    try {
      const commentData = {
        userName,
        content: newComment
      };
      
      const createdComment = await createComment(postId, commentData);
      setComments([...comments, createdComment]);
      setNewComment('');
    } catch (err) {
      console.error('댓글 작성 오류:', err);
      setError('댓글 작성에 실패했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };
  
  // 댓글 삭제 처리
  const handleDeleteComment = async (commentId) => {
    if (!window.confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
      return;
    }
    
    setIsDeleting(true);
    setError('');
    
    try {
      await deleteComment(postId, commentId);
      setComments(comments.filter(comment => comment.id !== commentId));
    } catch (err) {
      console.error('댓글 삭제 오류:', err);
      setError('댓글 삭제에 실패했습니다. 다시 시도해주세요.');
      
      // 404 오류의 경우 UI에서 항목 제거 (이미 삭제되었다고 가정)
      if (err.message && err.message.includes('404')) {
        setComments(comments.filter(comment => comment.id !== commentId));
        setError('댓글이 이미 삭제되었거나 존재하지 않습니다.');
      }
    } finally {
      setIsDeleting(false);
    }
  };
  
  // 날짜 포맷팅
  const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('ko-KR', options);
  };
  
  return (
    <div className="mt-6">
      <h3 className="font-bold text-lg mb-4">댓글</h3>
      
      {error && <p className="text-red-500 mb-4">{error}</p>}
      
      {isLoading ? (
        <p className="text-gray-500">댓글 불러오는 중...</p>
      ) : (
        <div className="space-y-4 mb-6">
          {comments.length === 0 ? (
            <p className="text-gray-500">아직 댓글이 없습니다.</p>
          ) : (
            comments.map(comment => (
              <div key={comment.id} className="bg-gray-50 p-4 rounded-lg">
                <div className="flex justify-between">
                  <span className="font-medium">{comment.userName}</span>
                  <div className="flex items-center space-x-2">
                    <span className="text-xs text-gray-500">{formatDate(comment.createdAt)}</span>
                    {comment.userName === userName && (
                      <button 
                        onClick={() => handleDeleteComment(comment.id)}
                        className="text-red-500 text-xs hover:text-red-700"
                        disabled={isDeleting}
                      >
                        {isDeleting ? '삭제 중...' : '삭제'}
                      </button>
                    )}
                  </div>
                </div>
                <p className="mt-2">{comment.content}</p>
              </div>
            ))
          )}
        </div>
      )}
      
      <form onSubmit={handleSubmitComment} className="mt-4">
        <div className="flex items-start space-x-2">
          <textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            className="flex-1 min-h-[80px] p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
            placeholder="댓글을 작성하세요..."
          />
          <Button
            type="submit"
            disabled={isSubmitting || !newComment.trim()}
            className="mt-1"
          >
            {isSubmitting ? '게시 중...' : '댓글 달기'}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default CommentSection;