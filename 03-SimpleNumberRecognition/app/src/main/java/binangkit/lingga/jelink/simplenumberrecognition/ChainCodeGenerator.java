package binangkit.lingga.jelink.simplenumberrecognition;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ChainCodeGenerator {

    public static final int[][] REPLACEMENT = {{1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}};

    private final int[] pixels;
    private final int width;
    private final int height;

    public ChainCodeGenerator(int[] pixels, int width, int height) {
        if (pixels.length != width * height) {
            throw new IllegalArgumentException("Pixels length must be equal to width * height");
        }
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }
    
    public List<Integer> generateChainCode(int x, int y) throws Exception {
        List<Integer> chainCode = new ArrayList<>();

        Point firstPosition = new Point(x, y);
        int firstDirection = 7;
        ChainCodeState firstState = new ChainCodeState(firstPosition, firstDirection);
        ChainCodeState state = new ChainCodeState(firstState);
        do {
            try {
                state = getNextState(state);
            } catch (Exception e) {
                throw e;
            }
            chainCode.add(state.getDirection());
        } while (!state.getPosition().equals(firstPosition));

        return chainCode;
    }

    private ChainCodeState getNextState(ChainCodeState currentState) {
        int currentDirection = currentState.getDirection();
        Point currentPosition = currentState.getPosition();
        int startNextDirection = ((currentDirection % 2 == 0 ? 7 : 6) + currentDirection) % 8;
        int endNextDirection = (startNextDirection + 7) % 8;

        for (int nextDirection = startNextDirection; nextDirection != endNextDirection;
             nextDirection = (nextDirection + 1) % 8) {
            Point neighbor = new Point(
                    currentPosition.x + REPLACEMENT[nextDirection][0],
                    currentPosition.y + REPLACEMENT[nextDirection][1]);
            if (isPointWithinBoundaryAndBlack(neighbor.x, neighbor.y)) {
                return new ChainCodeState(neighbor, nextDirection);
            }
        }

        throw new IllegalStateException("Unable to find next chain code");
    }
    
    public boolean isPointWithinBoundaryAndBlack(int x, int y) {
        return isPointWithinBoundary(x, y) && PatternRecognizerUtils.isBlack(pixels[y * width + x]);
    }

    private boolean isPointWithinBoundary(int x, int y) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    private static class ChainCodeState {

        private final Point position;
        private final int direction;

        public ChainCodeState(Point position, int direction) {
            this.position = position;
            this.direction = direction;
        }

        public ChainCodeState(ChainCodeState firstState) {
            this.position = firstState.getPosition();
            this.direction = firstState.getDirection();
        }

        public Point getPosition() {
            return position;
        }

        public int getDirection() {
            return direction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChainCodeState that = (ChainCodeState) o;

            if (direction != that.direction) return false;
            if (position != null ? !position.equals(that.position) : that.position != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = position != null ? position.hashCode() : 0;
            result = 31 * result + direction;
            return result;
        }
    }

}
