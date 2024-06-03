package GUI.utilities;

import GUI.piece.PIECE_ID;
import javafx.scene.image.Image;

import java.util.Objects;

public class ImageLoader {

    private static Image blackPawn;
    private static Image whitePawn;
    private static Image blackQueen;
    private static Image whiteQueen;
    private static Image blackKnight;
    private static Image whiteKnight;
    private static Image blackBishop;
    private static Image whiteBishop;
    private static Image blackKing;
    private static Image whiteKing;
    private static Image blackRook;
    private static Image whiteRook;

    static {
        blackPawn = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_pawn.png")));
        whitePawn = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_pawn.png")));
        blackQueen = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_queen.png")));
        whiteQueen = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_queen.png")));
        blackKnight = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_knight.png")));
        whiteKnight = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_knight.png")));
        blackBishop = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_bishop.png")));
        whiteBishop = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_bishop.png")));
        blackKing = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_king.png")));
        whiteKing = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_king.png")));
        blackRook = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_rook.png")));
        whiteRook = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_rook.png")));



    }
    public static Image getImage(PIECE_ID id, boolean isWhite) {
        if (isWhite) {
            return switch (id) {
                case PAWN -> ImageLoader.whitePawn;
                case ROOK -> ImageLoader.whiteRook;
                case KNIGHT -> ImageLoader.whiteKnight;
                case BISHOP -> ImageLoader.whiteBishop;
                case KING -> ImageLoader.whiteKing;
                case QUEEN -> ImageLoader.whiteQueen;
            };
        } else {
            return switch (id) {
                case PAWN -> ImageLoader.blackPawn;
                case ROOK -> ImageLoader.blackRook;
                case KNIGHT -> ImageLoader.blackKnight;
                case BISHOP -> ImageLoader.blackBishop;
                case KING -> ImageLoader.blackKing;
                case QUEEN -> ImageLoader.blackQueen;
            };
        }
    }
}
