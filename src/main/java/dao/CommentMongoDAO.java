package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import exceptions.CommentNotFoundException;
import exceptions.ForbiddenException;
import models.Comment;
import models.CommentDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import utils.CommentUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMongoDAO {

    private final MongoCollection<Document> commentsCollection;

    public CommentMongoDAO(MongoDatabase mongoDatabase, String collection) {
        this.commentsCollection = mongoDatabase.getCollection(collection);
    }

    public void createComment(Comment comment, String author) {
        Document commentDocument = new Document("content", comment.getContent())
                .append("postId", comment.getPostId())
                .append("authorId", comment.getAuthorId())
                .append("author", author)
                .append("commentedAt", new Date());

        commentsCollection.insertOne(commentDocument);
    }

    public List<CommentDocument> getAllCommentsByPostId(int postId) {
        Bson filter = Filters.eq("postId", postId);
        CommentUtils commentUtils = new CommentUtils();

        return commentsCollection.find(filter)
                .into(new ArrayList<>())
                .stream()
                .map(commentUtils::mapDocumentToComment)
                .collect(Collectors.toList());
    }

    public CommentDocument getCommentById(String commentId) {
        ObjectId objectId = new ObjectId(commentId);
        CommentUtils commentUtils = new CommentUtils();
        Document document = commentsCollection.find(Filters.eq("_id", objectId)).first();

        if (document == null) {
            throw new CommentNotFoundException("Not Found - Comment with ID " + commentId + " not found");
        }

        return commentUtils.mapDocumentToComment(document);
    }

    public boolean deleteComment(String commentId, int authorId) {
        ObjectId objectId = new ObjectId(commentId);
        DeleteResult result = commentsCollection.deleteOne(
                Filters.and(
                        Filters.eq("_id", objectId),
                        Filters.eq("authorId", authorId)
                )
        );
        if (result.getDeletedCount() == 0) {
            throw new ForbiddenException("Forbidden - You are not allowed to delete this comment or it does not exist.");
        }
        return result.getDeletedCount() > 0;
    }
}