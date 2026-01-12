package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoConnectionTest;
import exceptions.ForbiddenException;
import models.Comment;
import models.CommentDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentDAOTest {

    private static final String COLLECTION_NAME = "comments_test";

    private CommentDAO commentDAO;
    private MongoCollection<Document> testCommentCollection;

    @BeforeEach
    void setUp() {
        MongoDatabase mongoDatabase = MongoConnectionTest.getDatabase();
        commentDAO = new CommentDAO(mongoDatabase, COLLECTION_NAME);
        testCommentCollection = mongoDatabase.getCollection(COLLECTION_NAME);
    }

    @AfterEach
    void tearDown() {
        if (testCommentCollection != null) {
            testCommentCollection.drop();
        }
    }

    private Document createCommentDoc(String content, int postId, int authorId, String author) {
        return new Document("_id", new ObjectId())
                .append("content", content)
                .append("postId", postId)
                .append("authorId", authorId)
                .append("author", author)
                .append("commentedAt", new Date());
    }

    @ParameterizedTest
    @CsvSource({
            "Nice post!, 1, 101, John",
            "Great article, 2, 202, Alice",
            "Thanks for sharing, 3, 303, Bob"
    })
    void testCreateComment(String content, int postId, int authorId, String author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPostId(postId);
        comment.setAuthorId(authorId);

        commentDAO.createComment(comment, author);

        assertEquals(1, testCommentCollection.countDocuments());

        Document stored = testCommentCollection.find().first();
        assertNotNull(stored);
        assertEquals(content, stored.getString("content"));
        assertEquals(postId, stored.getInteger("postId"));
        assertEquals(authorId, stored.getInteger("authorId"));
        assertEquals(author, stored.getString("author"));
        assertNotNull(stored.getDate("commentedAt"));
    }

    @Test
    void testGetAllCommentsByPostId() {
        testCommentCollection.insertOne(createCommentDoc("First", 1, 101, "Alice"));
        testCommentCollection.insertOne(createCommentDoc("Second", 1, 102, "Bob"));
        testCommentCollection.insertOne(createCommentDoc("Other post", 2, 103, "John"));

        List<CommentDocument> comments =
                commentDAO.getAllCommentsByPostId(1);

        assertEquals(2, comments.size());
        assertTrue(comments.stream().allMatch(c -> c.getPostId() == 1));
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, 101, true",
            "Bob, 102, true"
    })
    void testDeleteComment_Success(String author, int authorId, boolean expected) {
        Document doc = createCommentDoc("Deletable", 1, authorId, author);
        testCommentCollection.insertOne(doc);

        boolean deleted = commentDAO.deleteComment(
                doc.getObjectId("_id").toHexString(),
                authorId
        );

        assertEquals(expected, deleted);
        assertEquals(0, testCommentCollection.countDocuments());
    }

    @Test
    void testDeleteComment_Forbidden() {
        Document doc = createCommentDoc("Protected", 1, 101, "Alice");
        testCommentCollection.insertOne(doc);

        assertThrows(
                ForbiddenException.class,
                () -> commentDAO.deleteComment(
                        doc.getObjectId("_id").toHexString(),
                        999
                )
        );

        assertEquals(1, testCommentCollection.countDocuments());
    }
}