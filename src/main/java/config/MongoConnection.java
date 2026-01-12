package config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {

    private static MongoClient mongoClient;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(MongoDBConfig.CONNECTION_STRING);
            System.out.println("Mongo DB Connection Established");
        }

        return mongoClient.getDatabase(MongoDBConfig.DATABASE_NAME);
    }

}
