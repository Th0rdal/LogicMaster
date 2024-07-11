package GUI.game;

import GUI.controller.AlertHandler;

import java.text.MessageFormat;

/**
 * Represent a given board position
 */
public class BoardCoordinate {

    // the locations are between 1 and 8!!!
    private final int xLocation;
    private final int yLocation;

    /**
     * constructor using x and y coordinates
     * @param x: the file of the position
     * @param y: the rank of the position
     */
    public BoardCoordinate(int x, int y) {
        xLocation = x;
        yLocation = y;
    }

    /**
     * copy constructor
     * @param copy: the BoardCoordinate to copy
     */
    public BoardCoordinate(BoardCoordinate copy) {
        this.xLocation = copy.getXLocation();
        this.yLocation = copy.getYLocation();
    }

    /**
     * Constructor to convert a string representation of a move (e.g., A1, H8, etc...) to a Boardcoordinate object
     * @param coordinate
     */
    public BoardCoordinate(String coordinate) {
        if (coordinate.equals("-")) {
            xLocation = -1;
            yLocation = -1;
        } else if (coordinate.length() != 2) {
            String message = MessageFormat.format("The coordinate string ({0}) has %d letters. Expected {1}", coordinate, coordinate.length());
            AlertHandler.throwError();
            throw new IllegalArgumentException(message);
        } else {
            int row = coordinate.charAt(0);
            if (Character.isDigit(row)) {
                xLocation = Integer.parseInt(coordinate);
            } else {
                if (Character.isLowerCase(row)) {
                    xLocation = Character.getNumericValue(row) - Character.getNumericValue('a') + 1;
                } else {
                    xLocation = Character.getNumericValue(row) - Character.getNumericValue('A') + 1;
                }
            }
            yLocation = Character.getNumericValue(coordinate.charAt(1));
        }
    }

    public int getLocationInt() {
        return xLocation + (yLocation - 1) * 8 - 1;
    }

    @Override
    public String toString() {
        if (xLocation == -1 && yLocation == -1) {
            return "-";
        }
        char xChar = (char) ('@' + xLocation);
        return "" + xChar + (char) (yLocation + '0');
    }

    /**
     * returns a string representing the current board position in lower case
     * @return: string representation in lower case
     */
    public String toLowerCaseString() {
        if (xLocation == -1 && yLocation == -1) {
            return "-";
        }
        char xChar = (char) ('`' + xLocation);
        return "" + xChar + (char) (yLocation + '0');
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoardCoordinate)) {
            AlertHandler.throwError();
            throw new IllegalArgumentException("the parameter must be of type BoardCoordinate");
        }
        return this.getXLocation() == ((BoardCoordinate) obj).getXLocation() &&
                this.getYLocation() == ((BoardCoordinate) obj).getYLocation();
    }

    /**
     * converts the board into byte representation
     * @return: byte representing the board coordinate (only uses the first 6 bits)
     */
    public byte convertToByte() {
        return (byte) ((byte) ((byte) (xLocation-1 & 0xff) << 3) | (byte) (yLocation-1 & 0xff));
    }

    public int getXLocation() {
        return xLocation;
    }

    public int getYLocation() {
        return yLocation;
    }

    public BoardCoordinate getLeft() {
        return new BoardCoordinate(xLocation - 1, yLocation);
    }

    public BoardCoordinate getRight() {
        return new BoardCoordinate(xLocation + 1, yLocation);
    }

    public BoardCoordinate getUp() {
        return new BoardCoordinate(xLocation, yLocation + 1);
    }

    public BoardCoordinate getDown() {
        return new BoardCoordinate(xLocation, yLocation - 1);
    }
}
