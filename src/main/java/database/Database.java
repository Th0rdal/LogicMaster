package database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class Database {

    private static final String DB_URL = "jdbc:h2:file: ./src/main/resources/database/db";
    private static final String username = "user";
    private static final String password = "password";
    private ConnectionSource connectionSource;
    private Dao<ChessGame, Long> dao;

    private static Database instance;

    private Database() {
        try {
            this.connectionSource = new JdbcConnectionSource(Database.DB_URL, Database.username, Database.password);
            TableUtils.createTableIfNotExists(this.connectionSource, ChessGame.class);

            this.dao = DaoManager.createDao(this.connectionSource, ChessGame.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Dao<ChessGame, Long> getDao() {
        return dao;
    }

    public void close() {
        if (connectionSource != null) {
            try {
                this.connectionSource.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        try {
            Database.getInstance().getDao().create(game);
            Database.getInstance().getDao().queryForId(game.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
