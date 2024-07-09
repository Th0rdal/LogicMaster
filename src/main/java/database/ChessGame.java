package database;

import GUI.game.gamestate.GAMESTATUS;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "chess_game")
public class ChessGame {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(dataType= DataType.BYTE_ARRAY, canBeNull = false)
    private byte[] moves;
    @DatabaseField(dataType= DataType.BYTE_ARRAY)
    private byte[] time;
    @DatabaseField(canBeNull = false)
    private String timeControl;
    @DatabaseField
    private boolean whiteTurn;
    @DatabaseField(canBeNull = false)
    private String whitePlayerName;
    @DatabaseField(canBeNull = false)
    private String blackPlayerName;
    @DatabaseField(canBeNull = false)
    private String whitePlayerPath;
    @DatabaseField(canBeNull = false)
    private String blackPlayerPath;
    @DatabaseField(canBeNull = false)
    private GAMESTATUS gameStatus;
    @DatabaseField(canBeNull = false)
    private Timestamp creationDatetime;
    @DatabaseField(canBeNull = false)
    private String startingFen;
    @DatabaseField
    private String endFen;

    public ChessGame() {
        this.moves = new byte[]{0x01, 0x02, 0x03};
        this.time = new byte[]{0x01, 0x02, 0x03};
        this.timeControl = "tc";
        this.whitePlayerName = "white";
        this.blackPlayerName = "black";
        this.whitePlayerPath = "whitePath";
        this.blackPlayerPath = "blackPath";
        this.gameStatus = GAMESTATUS.ONGOING;
        this.creationDatetime = new Timestamp(System.currentTimeMillis());
        this.startingFen = "start";
        this.endFen = "end";
    }

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

    public boolean isWhiteTurn() {return this.whiteTurn;}
}
