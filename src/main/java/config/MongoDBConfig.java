package config;

public class MongoDBConfig {
    public static final String CONNECTION_STRING = System.getenv("MONGODB_CONNECTION_STRING");
    public static final String DATABASE_NAME = "blog_db";
}
