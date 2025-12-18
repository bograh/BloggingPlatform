import dao.PostDAO;
import dao.UserDAO;
import models.User;
import services.PostService;
import services.UserService;


public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to Blogging Platform!!!");

        UserDAO userDAO = new UserDAO();
        UserService userService = new UserService(userDAO);

        User user = userService.signInUser();

        PostDAO postDAO = new PostDAO();
        PostService postService = new PostService(user, postDAO);

    }
}
