package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoConnectionTest;
import dao.CommentDAO;
import dtos.request.CreateCommentDTO;
import models.CommentDocument;
import models.User;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.CommentService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentControllerTest {

    private static final User TEST_USER =
            new User(1, "test", "test@email.com", "password", null);

    private static final String COLLECTION_NAME = "comment_test";

    private CommentService commentService;
    private CommentController commentController;
    private MongoCollection<Document> testCommentCollection;

    @BeforeEach
    void setUp() {
        MongoDatabase database = MongoConnectionTest.getDatabase();
        CommentDAO commentDAO = new CommentDAO(database, COLLECTION_NAME);
        commentService = new CommentService(TEST_USER, commentDAO);
        commentController = new CommentController(commentService);

        testCommentCollection = database.getCollection(COLLECTION_NAME);
    }

    @AfterEach
    void tearDown() {
        if (testCommentCollection != null) {
            testCommentCollection.drop();
        }
    }

    @Test
    void addCommentToPost_returnsSuccessMessage() {
        String response = commentController.addCommentToPost();
        assertEquals("Comment added to post successfully!!", response);
    }

    @Test
    void getAllCommentsByPostId_returnsComments() {
        commentService.addCommentToPost(new CreateCommentDTO("Test", 1));
        commentService.addCommentToPost(new CreateCommentDTO("Test2", 1));

        List<CommentDocument> comments = commentController.getAllCommentsByPostId(1);
        assertEquals(2, comments.size());
        assertEquals("Test", comments.getFirst().getContent());
    }

    @Test
    void getCommentById_returnsCorrectComment() {
        commentService.addCommentToPost(new CreateCommentDTO("Test", 1));
        CommentDocument doc = commentService.getAllCommentsByPostId(1).getFirst();

        CommentDocument result = commentController.getCommentById(doc.getId());
        assertNotNull(result);
        assertEquals(doc.getId(), result.getId());
    }

    @Test
    void getCommentById_returnsNullForUnknownId() {
        CommentDocument result = commentController.getCommentById("unknown");
        assertNull(result);
    }

    @Test
    void deleteComment_returnsServiceResponse() {
        String comment1 = insertComment(1, 1);
        String comment2 = insertComment(2, 99);

        String successfulResponse = commentController.deleteComment(comment1);
        assertTrue(successfulResponse.toLowerCase().contains("successfully"));

        String forbiddenResponse = commentController.deleteComment(comment2);
        assertTrue(forbiddenResponse.toLowerCase().contains("forbidden"));

        String unsuccessfulResponse = commentController.deleteComment("68105cfd7873c5e749b2d1a1");
        assertTrue(unsuccessfulResponse.toLowerCase().contains("not exist"));
    }

    private String insertComment(int postId, int authorId) {
        Document doc = new Document()
                .append("postId", postId)
                .append("authorId", authorId)
                .append("author", "testUser")
                .append("comment", "Test comment")
                .append("commentedAt", new Date());

        testCommentCollection.insertOne(doc);
        return doc.getObjectId("_id").toHexString();
    }

}