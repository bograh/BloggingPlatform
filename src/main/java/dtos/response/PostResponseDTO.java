package dtos.response;

public class PostResponseDTO {
    private int postId;
    private String title;
    private String body;
    private String author;
    private String lastUpdated;

    public PostResponseDTO() {
    }

    public PostResponseDTO(int postId, String title, String body, String author, String lastUpdated) {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.author = author;
        this.lastUpdated = lastUpdated;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    @Override
    public String toString() {
        return "PostResponseDTO{" +
                "postId=" + postId +
                ", postTitle='" + title + '\'' +
                ", postBody='" + body + '\'' +
                ", author='" + author + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}
