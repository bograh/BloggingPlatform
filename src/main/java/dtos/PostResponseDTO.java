package dtos;

public class PostResponseDTO {
    private int postId;
    private String postTitle;
    private String postBody;
    private String author;
    private String lastUpdated;

    public PostResponseDTO() {
    }

    public PostResponseDTO(int postId, String postTitle, String postBody, String author, String lastUpdated) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.author = author;
        this.lastUpdated = lastUpdated;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
