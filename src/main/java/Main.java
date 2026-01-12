import config.MongoConnection;
import config.PostgresConnectionProvider;
import controllers.CommentController;
import controllers.PostController;
import controllers.UserController;
import dao.CommentDAO;
import dao.PostDAO;
import dao.TagDAO;
import dao.UserDAO;
import dtos.request.CreateUserDTO;
import models.CommentDocument;
import models.User;
import services.CommentService;
import services.PostService;
import services.UserService;
import utils.Constants;
import utils.RandomUserGenerator;

import java.util.List;


public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to Blogging Platform!!!");

        PostgresConnectionProvider connectionProvider = new PostgresConnectionProvider();
        UserDAO userDAO = new UserDAO(connectionProvider);

        PostDAO postDAO = new PostDAO(connectionProvider);
        TagDAO tagDAO = new TagDAO(connectionProvider);
        CommentDAO commentDAO = new CommentDAO(
                MongoConnection.getDatabase(),
                Constants.CommentsMongoCollection
        );

        System.out.println(commentDAO.getAllCommentsByPostId(1));
        System.out.println();

        UserService userService = new UserService(userDAO);

        UserController userController = new UserController(userService);

        CreateUserDTO createUserDTO = RandomUserGenerator.randomUser();
        String email = "ben@email.com";
        String password = "password1";

        userController.registerUser(createUserDTO);
        User user = userController.signInUser(email, password);

        PostService postService = new PostService(user, postDAO, tagDAO);
        CommentService commentService = new CommentService(user, commentDAO);


        PostController postController = new PostController(postService);
        CommentController commentController = new CommentController(commentService);

        List<CommentDocument> comments = commentController.getAllCommentsByPostId(1);
        System.out.println(comments);

        System.out.println(commentController.getAllCommentsByPostId(1));
        System.out.println(commentController.getAllCommentsByPostId(1));
        System.out.println(commentController.getAllCommentsByPostId(1));


        /*int i = 3;
        while (i > 0) {
            userController.registerUser();
            userController.signInUser();
            postController.createPost(new CreatePostDTO());
            List<PostResponseDTO> posts = postController.getAllPosts();
            postController.getPostById(1);
            postController.updatePost(3);
            postController.deletePost(posts.stream()
                    .mapToInt(PostResponseDTO::getPostId)
                    .max()
                    .orElse(0));

            commentController.addCommentToPost();
            List<CommentDocument> comments = commentController.getAllCommentsByPostId(1);
            System.out.println(comments);

            i--;
        }*/


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