import config.PostgresConnectionProvider;
import controllers.CommentController;
import controllers.PostController;
import controllers.UserController;
import dao.CommentDAO;
import dao.PostDAO;
import dao.TagDAO;
import dao.UserDAO;
import models.User;
import services.CommentService;
import services.PostService;
import services.UserService;


public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to Blogging Platform!!!");

        PostgresConnectionProvider connectionProvider = new PostgresConnectionProvider();
        UserDAO userDAO = new UserDAO(connectionProvider);

        PostDAO postDAO = new PostDAO(connectionProvider);
        TagDAO tagDAO = new TagDAO(connectionProvider);
        CommentDAO commentDAO = new CommentDAO(connectionProvider);

        UserService userService = new UserService(userDAO);


        UserController userController = new UserController(userService);
        userController.registerUser();
        User user = userController.signInUser();

        PostService postService = new PostService(user, postDAO, tagDAO);
        CommentService commentService = new CommentService(user, commentDAO);


        PostController postController = new PostController(postService);
        CommentController commentController = new CommentController(commentService);

//        postService.getAllPosts();

        /*postService.getPostById(1);
        postService.getPostById(3);
        postService.getPostById(999);
        postService.getPostById(2);
        postService.getPostById(222);

        postService.updatePost(1);
        postService.updatePost(3);


        postService.deletePost(1);
        postService.deletePost(4);*/

    }
}
