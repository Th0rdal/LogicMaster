package GUI;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Circle extends javafx.scene.shape.Circle{

    private static int circleRadius = 15;
    private static Color circleColor = Color.web("#C6EDC3");

    public Circle() {
        this.setRadius(circleRadius);
        this.setFill(circleColor);
        this.setStroke(null);
        this.setMouseTransparent(true);
    }
}
