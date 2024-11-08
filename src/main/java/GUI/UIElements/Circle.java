package GUI.UIElements;

import javafx.scene.paint.Color;

/**
 * represents a circle used to show the player what moves are possible
 */
public class Circle extends javafx.scene.shape.Circle{

    private static int circleRadius = 15;
    private static Color circleColor = Color.web("#C6EDC3");

    public Circle() {
        super();
        this.setRadius(circleRadius);
        this.setFill(circleColor);
        this.setStroke(null);
        this.setMouseTransparent(true);
    }
}
