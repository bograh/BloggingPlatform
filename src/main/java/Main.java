import dao.Database;
import ui.BloggingApp;

public class Main {

    public static void main(String[] args) {
        Database.initDatabase();
        BloggingApp.init(args);
    }
}