package models;

import java.time.LocalDateTime;

public class Review {
    private int id;
    private int postId;
    private int reviewerId;
    private int ratings;
    private String review;
    private LocalDateTime createdAt;

    public Review() {
    }

    public Review(int id, int postId, int reviewerId, int ratings, String review, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.reviewerId = reviewerId;
        this.ratings = ratings;
        this.review = review;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{id=" + id + ", postId=" + postId + ", userId=" + reviewerId +
                ", rating=" + ratings + ", createdAt=" + createdAt + '}';
    }
}
