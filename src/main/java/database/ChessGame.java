package database;

import GUI.game.gamestate.GAMESTATUS;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

/**
 * represents the database table with each DatabaseField being an attribute of the table.
 */
@DatabaseTable(tableName = "chess_game")
public class ChessGame {
    /** only used for identification*/
    @DatabaseField(generatedId = true)
    private long id;

    /** all moves made in the game */
    @DatabaseField(dataType= DataType.BYTE_ARRAY, canBeNull = false)
    private byte[] moves;

    /** a list of the time remaining after a move was made */
    @DatabaseField(dataType= DataType.BYTE_ARRAY)
    private byte[] time;

    /** the time control used */
    @DatabaseField(canBeNull = false)
    private String timeControl;

    /** the side that whose turn it is */
    @DatabaseField
    private boolean whiteTurn;

    /** the name of the white player */
    @DatabaseField(canBeNull = false)
    private String whitePlayerName;

    /** the name of the black player */
    @DatabaseField(canBeNull = false)
    private String blackPlayerName;

    /** the path to the algorithm used. Empty string if the player was a human */
    @DatabaseField(canBeNull = false)
    private String whitePlayerPath;

    /** the path to the algorithm used. Empty string if the player was a human */
    @DatabaseField(canBeNull = false)
    private String blackPlayerPath;

    /** the current gamestatus of the game */
    @DatabaseField(canBeNull = false)
    private GAMESTATUS gameStatus;

    /** the creation timestamp of when the entry was added to the database */
    @DatabaseField(canBeNull = false)
    private Timestamp creationDatetime;

    /** the fen notation the chess game was started on */
    @DatabaseField(canBeNull = false)
    private String startingFen;

    /** the fen notation the chess game was in before saving */
    @DatabaseField
    private String endFen;

    /**
     * ChessGame constructor
     * optimization: create byte[] moves here and take ArrayList as argument, pass Player object insteead of the 4 strings
     * @param moves: moves in form of byte array
     * @param time: time in form of byte array
     * @param timeControl: the time control string representing the time control configuration of the game
     * @param whitePlayerName: the white player name
     * @param blackPlayerName: the black player name
     * @param whitePlayerPath: the path to the algorithm used (give empty string if the player is a human)
     * @param blackPlayerPath: the path to the algorithm used (give empty string if the player is a human)
     * @param gameStatus: the current status of the game
     * @param turn: the current side whose turn it is
     * @param startingFen: the fen notation that started the game
     * @param endFen: the fen notation that the board was in when it was saved
     */

    public ChessGame() {}

    public ChessGame(
            byte[] moves,
            byte[] time,
            String timeControl,
            String whitePlayerName,
            String blackPlayerName,
            String whitePlayerPath,
            String blackPlayerPath,
            GAMESTATUS gameStatus,
            boolean turn,
            String startingFen,
            String endFen) {

        this.moves = moves;
        this.time = time;
        this.timeControl = timeControl;
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.whitePlayerPath = whitePlayerPath;
        this.blackPlayerPath = blackPlayerPath;
        this.gameStatus = gameStatus;
        this.whiteTurn = turn;
        this.startingFen = startingFen;
        this.endFen = endFen;
        this.creationDatetime = new Timestamp(System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public byte[] getMoves() {
        return moves;
    }

    public byte[] getTime() {
        return time;
    }

    public String getTimeControl() {
        return timeControl;
    }

    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public String getWhitePlayerPath() {
        return whitePlayerPath;
    }

    public String getBlackPlayerPath() {
        return blackPlayerPath;
    }

    public GAMESTATUS getGameStatus() {
        return gameStatus;
    }

    public Timestamp getCreationDatetime() {
        return creationDatetime;
    }

    public String getStartingFen() {
        return startingFen;
    }

    public String getEndFen() {
        return endFen;
    }

    public boolean isWhiteTurn() {
        return this.whiteTurn;
    }
}
