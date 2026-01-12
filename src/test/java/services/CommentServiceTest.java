package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoConnectionTest;
import dao.CommentMongoDAO;
import dtos.request.CreateCommentDTO;
import models.CommentDocument;
import models.User;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTest {

    private static final String COLLECTION_NAME = "comments_test";
    private static final User TEST_USER =
            new User(1, "test", "test@test.com", "pass", null);

    private CommentService commentService;
    private MongoCollection<Document> testCommentCollection;

    @BeforeEach
    void setUp() {
        MongoDatabase database = MongoConnectionTest.getDatabase();

        CommentMongoDAO commentMongoDAO = new CommentMongoDAO(
                database,
                COLLECTION_NAME
        );

        testCommentCollection = database.getCollection(COLLECTION_NAME);

        commentService = new CommentService(TEST_USER, commentMongoDAO);
    }

    @AfterEach
    void tearDown() {
        if (testCommentCollection != null) {
            testCommentCollection.drop();
        }
    }

    @Test
    void addCommentToPost_success() {
        CreateCommentDTO dto = new CreateCommentDTO("Nice post", 1);

        String result = commentService.addCommentToPost(dto);

        assertEquals(
                "Comment added to post successfully!!",
                result
        );

        assertEquals(1, testCommentCollection.countDocuments());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    void getAllCommentsByPostId_cacheHit(int postId) {
        insertComment(postId, 1);
        insertComment(postId, 2);

        List<CommentDocument> first =
                commentService.getAllCommentsByPostId(postId);

        List<CommentDocument> second =
                commentService.getAllCommentsByPostId(postId);

        assertEquals(2, first.size());
        assertSame(first, second);
    }

    @Test
    void getAllCommentsByPostId_exception_returnsEmptyList() {
        testCommentCollection.drop();

        List<CommentDocument> result =
                commentService.getAllCommentsByPostId(99);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getCommentById_notFound_returnsNull() {
        CommentDocument result =
                commentService.getCommentById(
                        "507f1f77bcf86cd799439011"
                );

        assertNull(result);
    }

    @Test
    void deleteComment_success() {
        String commentId = insertComment(1, TEST_USER.getId());

        String result =
                commentService.deleteComment(commentId);

        assertEquals(
                "Comment with id: " + commentId + " deleted successfully!",
                result
        );

        assertEquals(0, testCommentCollection.countDocuments());
    }

    @Test
    void deleteComment_forbidden_returnsMessage() {
        String commentId = insertComment(1, 99);

        String result = commentService.deleteComment(commentId);
        assertTrue(result.toLowerCase().contains("forbidden"));
    }

    @Test
    void clearAllCaches_doesNotThrow() {
        assertDoesNotThrow(() -> commentService.clearAllCaches());
    }

    @Test
    void getCacheStatistics_returnsText() {
        String stats = commentService.getCacheStatistics();

        assertTrue(stats.contains("Comment Cache"));
        assertTrue(stats.contains("Comment List Cache"));
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