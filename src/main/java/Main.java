import dao.PostDAO;
import dao.TagDAO;
import dao.UserDAO;
import models.User;
import services.PostService;
import services.UserService;


public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to Blogging Platform!!!");

        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();
        TagDAO tagDAO = new TagDAO();

        UserService userService = new UserService(userDAO);

        User user = userService.signInUser();

        PostService postService = new PostService(user, postDAO, tagDAO);

//        postService.getAllPosts();

        postService.getPostById(1);
        postService.getPostById(3);
        postService.getPostById(999);
        postService.getPostById(2);
        postService.getPostById(222);

        postService.updatePost(1);
        postService.updatePost(3);

    }
}
