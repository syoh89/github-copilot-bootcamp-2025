from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime
from sqlalchemy import create_engine, Column, Integer, String, DateTime, event
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session

app = FastAPI()

# Database setup
DATABASE_URL = "sqlite:///./sns.db"  # 상대 경로로 수정
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
Base = declarative_base()
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Models
class Post(BaseModel):
    id: int
    userName: str
    content: str
    createdAt: datetime
    updatedAt: datetime
    likeCount: int
    commentCount: int

    class Config:
        orm_mode = True

class CreatePostRequest(BaseModel):
    userName: str
    content: str

class UpdatePostRequest(BaseModel):
    content: str

class Comment(BaseModel):
    id: int
    postId: int
    userName: str
    content: str
    createdAt: datetime
    updatedAt: datetime

class CreateCommentRequest(BaseModel):
    userName: str
    content: str

class UpdateCommentRequest(BaseModel):
    content: str

class LikeRequest(BaseModel):
    userName: str

class ErrorResponse(BaseModel):
    message: str

# Database Models
class PostDB(Base):
    __tablename__ = "posts"

    id = Column(Integer, primary_key=True, index=True)
    userName = Column(String, index=True)
    content = Column(String)
    createdAt = Column(DateTime)
    updatedAt = Column(DateTime)
    likeCount = Column(Integer, default=0)
    commentCount = Column(Integer, default=0)

class CommentDB(Base):
    __tablename__ = "comments"

    id = Column(Integer, primary_key=True, index=True)
    postId = Column(Integer, index=True)
    userName = Column(String, index=True)
    content = Column(String)
    createdAt = Column(DateTime)
    updatedAt = Column(DateTime)

class LikeDB(Base):
    __tablename__ = "likes"

    id = Column(Integer, primary_key=True, index=True)
    postId = Column(Integer, index=True)
    userName = Column(String, index=True)

@event.listens_for(Base.metadata, 'before_create')
def create_tables_with_if_not_exists(target, connection, **kw):
    connection.execute('PRAGMA foreign_keys=ON')
    for table in target.tables.values():
        connection.execute(f'CREATE TABLE IF NOT EXISTS {table.name} ({", ".join(f"{col.name} {col.type}" for col in table.columns)})')

# Create tables
Base.metadata.create_all(bind=engine)

# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Post Endpoints
@app.get("/api/posts", response_model=List[Post])
def get_posts(db: Session = Depends(get_db)):
    return db.query(PostDB).all()

@app.post("/api/posts", response_model=Post, status_code=201)
def create_post(request: CreatePostRequest, db: Session = Depends(get_db)):
    new_post = PostDB(
        userName=request.userName,
        content=request.content,
        createdAt=datetime.now(),
        updatedAt=datetime.now(),
        likeCount=0,
        commentCount=0
    )
    db.add(new_post)
    db.commit()
    db.refresh(new_post)
    return new_post

@app.get("/api/posts/{postId}", response_model=Post)
def get_post(postId: int, db: Session = Depends(get_db)):
    post = db.query(PostDB).filter(PostDB.id == postId).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    return post

@app.patch("/api/posts/{postId}", response_model=Post)
def update_post(postId: int, request: UpdatePostRequest, db: Session = Depends(get_db)):
    post = db.query(PostDB).filter(PostDB.id == postId).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    post.content = request.content
    post.updatedAt = datetime.now()
    db.commit()
    db.refresh(post)
    return post

@app.delete("/api/posts/{postId}", status_code=204)
def delete_post(postId: int, db: Session = Depends(get_db)):
    post = db.query(PostDB).filter(PostDB.id == postId).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    db.delete(post)
    db.commit()

# Comment Endpoints
@app.get("/api/posts/{postId}/comments", response_model=List[Comment])
def get_comments(postId: int, db: Session = Depends(get_db)):
    post = db.query(PostDB).filter(PostDB.id == postId).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    return db.query(CommentDB).filter(CommentDB.postId == postId).all()

@app.post("/api/posts/{postId}/comments", response_model=Comment, status_code=201)
def create_comment(postId: int, request: CreateCommentRequest, db: Session = Depends(get_db)):
    post = db.query(PostDB).filter(PostDB.id == postId).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    new_comment = CommentDB(
        postId=postId,
        userName=request.userName,
        content=request.content,
        createdAt=datetime.now(),
        updatedAt=datetime.now()
    )
    db.add(new_comment)
    post.commentCount += 1
    db.commit()
    db.refresh(new_comment)
    return new_comment

@app.get("/api/posts/{postId}/comments/{commentId}", response_model=Comment)
def get_comment(postId: int, commentId: int, db: Session = Depends(get_db)):
    comment = db.query(CommentDB).filter(
        CommentDB.id == commentId,
        CommentDB.postId == postId
    ).first()
    if not comment:
        raise HTTPException(status_code=404, detail="Comment not found")
    return comment

@app.patch("/api/posts/{postId}/comments/{commentId}", response_model=Comment)
def update_comment(postId: int, commentId: int, request: UpdateCommentRequest, db: Session = Depends(get_db)):
    comment = db.query(CommentDB).filter(
        CommentDB.id == commentId,
        CommentDB.postId == postId
    ).first()
    if not comment:
        raise HTTPException(status_code=404, detail="Comment not found")
    
    comment.content = request.content
    comment.updatedAt = datetime.now()
    db.commit()
    db.refresh(comment)
    return comment

@app.delete("/api/posts/{postId}/comments/{commentId}", status_code=204)
def delete_comment(postId: int, commentId: int, db: Session = Depends(get_db)):
    comment = db.query(CommentDB).filter(
        CommentDB.id == commentId,
        CommentDB.postId == postId
    ).first()
    if not comment:
        raise HTTPException(status_code=404, detail="Comment not found")
    
    post = db.query(PostDB).filter(PostDB.id == postId).first()
    if post:
        post.commentCount -= 1
    
    db.delete(comment)
    db.commit()

# Like Endpoints
@app.post("/api/posts/{postId}/likes", status_code=201)
def like_post(postId: int, request: LikeRequest, db: Session = Depends(get_db)):
    post = db.query(PostDB).filter(PostDB.id == postId).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    existing_like = db.query(LikeDB).filter(
        LikeDB.postId == postId,
        LikeDB.userName == request.userName
    ).first()
    
    if existing_like:
        raise HTTPException(status_code=400, detail="Already liked")
    
    new_like = LikeDB(postId=postId, userName=request.userName)
    db.add(new_like)
    post.likeCount += 1
    db.commit()
    return {"message": "Like added"}

@app.delete("/api/posts/{postId}/likes", status_code=204)
def unlike_post(postId: int, request: LikeRequest, db: Session = Depends(get_db)):
    post = db.query(PostDB).filter(PostDB.id == postId).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    like = db.query(LikeDB).filter(
        LikeDB.postId == postId,
        LikeDB.userName == request.userName
    ).first()
    
    if not like:
        raise HTTPException(status_code=404, detail="Like not found")
    
    db.delete(like)
    post.likeCount -= 1
    db.commit()