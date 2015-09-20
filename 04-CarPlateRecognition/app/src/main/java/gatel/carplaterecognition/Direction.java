package gatel.carplaterecognition;

import android.graphics.Point;

public enum Direction {
    NORTH(0, 1),
    NORTHEAST(1, 1),
    EAST(1, 0),
    SOUTHEAST(1, -1),
    SOUTH(0, -1),
    SOUTHWEST(-1, -1),
    WEST(-1, 0),
    NORTHWEST(-1, 1);

    private final Point replacement;

    Direction(Point replacement) {
        this.replacement = replacement;
    }

    Direction(int x, int y) {
        this.replacement = new Point(x, y);
    }
}
