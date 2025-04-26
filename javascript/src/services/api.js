import {
  mockGetPosts,
  mockGetPost,
  mockCreatePost,
  mockUpdatePost,
  mockDeletePost,
  mockGetComments,
  mockCreateComment,
  mockLikePost,
  mockUnlikePost
} from './mockData';

const API_URL = '/api';
const USE_REAL_API = true;

// API 오류 처리를 위한 헬퍼 함수
const handleApiError = async (response, defaultMessage) => {
  if (!response.ok) {
    let errorMessage = defaultMessage;
    
    try {
      // 서버에서 오류 메시지를 제공하는 경우 사용
      const errorData = await response.json();
      if (errorData.message) {
        errorMessage = errorData.message;
      } else if (errorData.detail) {
        errorMessage = errorData.detail;
      }
    } catch (e) {
      // 응답이 JSON이 아닌 경우
      console.error('Error parsing API error response', e);
    }
    
    // HTTP 상태 코드별 특정 처리를 위한 정보 포함
    const error = new Error(errorMessage);
    error.status = response.status;
    error.statusText = response.statusText;
    throw error;
  }
};

export const getPosts = async () => {
  if (USE_REAL_API) {
    try {
      const response = await fetch(`${API_URL}/posts`);
      await handleApiError(response, '포스트를 불러오는데 실패했습니다');
      return response.json();
    } catch (error) {
      console.error('API Error:', error);
      throw error; // 상세 오류 정보 유지
    }
  } else {
    return mockGetPosts();
  }
};

export const getPost = async (postId) => {
  if (USE_REAL_API) {
    try {
      const response = await fetch(`${API_URL}/posts/${postId}`);
      await handleApiError(response, '포스트를 불러오는데 실패했습니다');
      return response.json();
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    return mockGetPost(postId);
  }
};

export const createPost = async (postData) => {
  if (USE_REAL_API) {
    try {
      const response = await fetch(`${API_URL}/posts`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(postData)
      });
      await handleApiError(response, '포스트 생성에 실패했습니다');
      return response.json();
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    return mockCreatePost(postData);
  }
};

export const updatePost = async (postId, postData) => {
  if (USE_REAL_API) {
    try {
      const response = await fetch(`${API_URL}/posts/${postId}`, {
        method: 'PATCH', // POST 대신 PATCH 사용 (부분 업데이트)
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(postData)
      });
      await handleApiError(response, '포스트 수정에 실패했습니다');
      return response.json();
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    return mockUpdatePost(postId, postData);
  }
};

export const deletePost = async (postId) => {
  if (USE_REAL_API) {
    try {
      const response = await fetch(`${API_URL}/posts/${postId}`, {
        method: 'DELETE'
      });
      await handleApiError(response, '포스트 삭제에 실패했습니다');
      return true;
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    return mockDeletePost(postId);
  }
};

export const getComments = async (postId) => {
  if (USE_REAL_API) {
    try {
      // 명시적으로 숫자로 변환하여 타입 불일치 방지
      const numericPostId = Number(postId);
      
      const response = await fetch(`${API_URL}/posts/${numericPostId}/comments`);
      await handleApiError(response, '댓글을 불러오는데 실패했습니다');
      return response.json();
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    return mockGetComments(postId);
  }
};

export const createComment = async (postId, commentData) => {
  if (USE_REAL_API) {
    try {
      // 명시적으로 숫자로 변환하여 타입 불일치 방지
      const numericPostId = Number(postId);
      
      const response = await fetch(`${API_URL}/posts/${numericPostId}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(commentData)
      });
      await handleApiError(response, '댓글 작성에 실패했습니다');
      return response.json();
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    return mockCreateComment(postId, commentData);
  }
};

export const deleteComment = async (postId, commentId) => {
  if (USE_REAL_API) {
    try {
      // 명시적으로 숫자로 변환하여 타입 불일치 방지
      const numericPostId = Number(postId);
      const numericCommentId = Number(commentId);
      
      const response = await fetch(`${API_URL}/posts/${numericPostId}/comments/${numericCommentId}`, {
        method: 'DELETE'
      });
      await handleApiError(response, '댓글 삭제에 실패했습니다');
      return true;
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    // Mock 구현은 생략
    return true;
  }
};

export const likePost = async (postId, userData) => {
  if (USE_REAL_API) {
    try {
      // 명시적으로 숫자로 변환하여 타입 불일치 방지
      const numericPostId = Number(postId);
      
      const response = await fetch(`${API_URL}/posts/${numericPostId}/likes`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
      });
      await handleApiError(response, '좋아요에 실패했습니다');
      return true;
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    return mockLikePost(postId, userData);
  }
};

export const unlikePost = async (postId, userData) => {
  if (USE_REAL_API) {
    try {
      // 명시적으로 숫자로 변환하여 타입 불일치 방지
      const numericPostId = Number(postId);
      
      const response = await fetch(`${API_URL}/posts/${numericPostId}/likes?userName=${encodeURIComponent(userData.userName)}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      await handleApiError(response, '좋아요 취소에 실패했습니다');
      return true;
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  } else {
    return mockUnlikePost(postId, userData);
  }
};