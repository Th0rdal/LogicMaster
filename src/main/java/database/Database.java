package database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * represents the connection to the database. It is implemented as a singleton
 */
public class Database {

    private static final String DB_URL = "jdbc:h2:file: ./src/main/resources/database/db";
    private static final String username = "user";
    private static final String password = "password";
    private final ConnectionSource connectionSource;
    private final Dao<ChessGame, Long> dao;

    private static Database instance;

    /**
     * database constructor
     */
    private Database() {
        try {
            this.connectionSource = new JdbcConnectionSource(Database.DB_URL, Database.username, Database.password);
            TableUtils.createTableIfNotExists(this.connectionSource, ChessGame.class);

            this.dao = DaoManager.createDao(this.connectionSource, ChessGame.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get the Database instance and create one if it is not yet created
     * @return Database object
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Dao<ChessGame, Long> getDao() {
        return dao;
    }

}
