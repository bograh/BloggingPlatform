package services;

import dao.PostDAO;
import dao.TagDAO;
import dtos.request.CreatePostDTO;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import exceptions.ForbiddenException;
import exceptions.PostNotFoundException;
import models.Post;
import models.Tag;
import models.User;
import utils.Constants;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostService {

    private final User user;
    private final PostDAO postDAO;
    private final TagDAO tagDAO;

    private final CacheService<Integer, PostResponseDTO> postCache = new CacheService<>(300_000);
    private final CacheService<String, List<PostResponseDTO>> postsListCache = new CacheService<>(300_000);

    public PostService(User user, PostDAO postDAO, TagDAO tagDAO) {
        this.user = user;
        this.postDAO = postDAO;
        this.tagDAO = tagDAO;
    }

    public String createPost(CreatePostDTO createPostDTO, List<String> tagsList) {
        Post post = new Post(
                1,
                createPostDTO.getTitle(),
                createPostDTO.getBody(),
                user.getId(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        try {
            List<Tag> tags = tagDAO.getAllTagsFromList(tagsList);
            List<Integer> tagIds = new ArrayList<>(tags.stream().map(Tag::getId).toList());
            postDAO.addPost(post, tagIds);

            postsListCache.clear();
            return "Blog post created successfully!!";
        } catch (SQLException e) {
            System.out.printf("Error occurred while creating post: %s\n", e.getMessage());
        }
        return "An error occurred while creating post";
    }

    public List<PostResponseDTO> getAllPosts() {
        Optional<List<PostResponseDTO>> cachedPosts = postsListCache.get(Constants.AllPostsCacheKey);
        if (cachedPosts.isPresent()) {
            System.out.println("[CACHE HIT] Retrieved all posts from cache");
            return cachedPosts.get();
        }

        System.out.println("[CACHE MISS] Fetching all posts from database");
        try {
            List<PostResponseDTO> posts = postDAO.getAllPosts();
            postsListCache.set(Constants.AllPostsCacheKey, posts);
            return posts;
        } catch (SQLException e) {
            System.out.printf(
                    "An error occurred while retrieving posts: %s%n",
                    e.getMessage()
            );
            return new ArrayList<>();
        }
    }

    public PostResponseDTO getPostById(int postId) {
        Optional<PostResponseDTO> cachedPost = postCache.get(postId);
        if (cachedPost.isPresent()) {
            System.out.printf("[CACHE HIT] Retrieved post %d from cache\n", postId);
            return cachedPost.get();
        }

        System.out.printf("[CACHE MISS] Fetching post %d from database\n", postId);
        try {
            PostResponseDTO post = postDAO.getPostById(postId);
            postCache.set(post.getPostId(), post);
            return post;
        } catch (PostNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.printf("An error occurred while retrieving post with id: %d\n%s\n", postId, e.getMessage());
        }
        return null;
    }

    public String updatePost(UpdatePostDTO updatePostDTO) {
        try {
            postDAO.updatePost(updatePostDTO, user.getId());

            postCache.invalidate(updatePostDTO.getId());
            postsListCache.clear();

            return String.format("Post with id: %d updated successfully!", updatePostDTO.getId());

        } catch (ForbiddenException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (SQLException e) {
            System.out.printf("An error occurred while updating post with id: %d\n%s\n", updatePostDTO.getId(), e.getMessage());
        }
        return String.format("An error occurred while updating post with id: %d", updatePostDTO.getId());
    }

    public String deletePost(int postId) {
        try {

            postDAO.deletePost(postId, user.getId());

            postCache.invalidate(postId);
            postsListCache.clear();

            return String.format("Post with id: %d deleted successfully!\n", postId);

        } catch (ForbiddenException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (SQLException e) {
            System.out.printf("An error occurred while deleting post with id: %d\n%s\n", postId, e.getMessage());
        }
        return String.format("An error occurred while deleting post with id: %d", postId);
    }

    public List<PostResponseDTO> searchPosts(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }

        try {
            return postDAO.searchPosts(query);
        } catch (SQLException e) {
            System.out.printf(
                    "An error occurred while performing search: %s%n",
                    e.getMessage()
            );
            return new ArrayList<>();
        }
    }

    public List<PostResponseDTO> searchPostsByTag(String tagName) {
        if (tagName == null || tagName.isBlank()) {
            return new ArrayList<>();
        }

        String cacheKey = "search_tag_" + tagName.toLowerCase();
        Optional<List<PostResponseDTO>> cachedResults = postsListCache.get(cacheKey);
        if (cachedResults.isPresent()) {
            System.out.println("[CACHE HIT] Retrieved tag search results from cache");
            return cachedResults.get();
        }

        System.out.println("[CACHE MISS] Fetching tag search results from database");
        try {
            List<PostResponseDTO> results = postDAO.searchPostsByTag(tagName);
            if (!results.isEmpty()) {
                postsListCache.set(cacheKey, results);
            }
            return results;
        } catch (SQLException e) {
            System.out.printf("An error occurred while searching posts by tag: %s%n", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<PostResponseDTO> searchPostsByAuthor(String authorUsername) {
        if (authorUsername == null || authorUsername.isBlank()) {
            return new ArrayList<>();
        }

        String cacheKey = "search_author_" + authorUsername.toLowerCase();
        Optional<List<PostResponseDTO>> cachedResults = postsListCache.get(cacheKey);
        if (cachedResults.isPresent()) {
            System.out.println("[CACHE HIT] Retrieved author search results from cache");
            return cachedResults.get();
        }

        System.out.println("[CACHE MISS] Fetching author search results from database");
        try {
            List<PostResponseDTO> results = postDAO.searchPostsByAuthor(authorUsername);
            if (!results.isEmpty()) {
                postsListCache.set(cacheKey, results);
            }
            return results;
        } catch (SQLException e) {
            System.out.printf("An error occurred while searching posts by author: %s%n", e.getMessage());
            return new ArrayList<>();
        }
    }

    public String getCacheStatistics() {
        return "Post Cache: " + postCache.getStatistics() + "\n" +
                "List Cache: " + postsListCache.getStatistics();
    }

    public void clearAllCaches() {
        postCache.clear();
        postsListCache.clear();
    }

}