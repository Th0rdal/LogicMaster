package GUI.shapes;

import GUI.utilities.BoardCoordinate;
import javafx.scene.layout.StackPane;

/**
 * Represents a square of the Board grid
 */
public class Square extends StackPane{

    private BoardCoordinate coordinate;

    public Square(int x, int y) {
        /**
         * @param x: x coordinate of the square
         * @param y: y coordinate of the square
         */
        this.coordinate = new BoardCoordinate(x, y);
    }

    public int getCoordinates() {
        /**
         * Returns the coordinates of the square as a single integer variable in form XY
         */
        return this.coordinate.getLocationInt();
    }
}
