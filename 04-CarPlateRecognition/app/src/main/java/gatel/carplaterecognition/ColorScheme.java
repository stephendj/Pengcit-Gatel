package gatel.carplaterecognition;

public class ColorScheme {

    public static final ColorScheme DEFAULT_COLOR_SCHEME = new ColorScheme(Type.BLACK_ON_WHITE, 0x50);

    public static enum Type {
        BLACK_ON_WHITE,
        WHITE_ON_BLACK
    }

    private final int threshold;
    private final Type type;

    public ColorScheme(Type type, int threshold) {
        this.threshold = threshold;
        this.type = type;
    }

    public boolean isForeground(int color) {
        switch (type) {
            case BLACK_ON_WHITE:
                return isBlack(color);
            case WHITE_ON_BLACK:
                return isWhite(color);
            default:
                throw new IllegalArgumentException("Unknown ColorScheme type: " + type);
        }
    }

    public boolean isBackground(int color) {
        return !isForeground(color);
    }

    public int getForeground() {
        switch (type) {
            case BLACK_ON_WHITE:
                return ColorUtils.BLACK;
            case WHITE_ON_BLACK:
                return ColorUtils.WHITE;
            default:
                throw new IllegalArgumentException("Unknown ColorScheme type: " + type);
        }
    }

    public int getBackground() {
        switch (type) {
            case BLACK_ON_WHITE:
                return ColorUtils.WHITE;
            case WHITE_ON_BLACK:
                return ColorUtils.BLACK;
            default:
                throw new IllegalArgumentException("Unknown ColorScheme type: " + type);
        }
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean isWhite(int color) {
        return ColorUtils.getGrayscale(color) > threshold;
    }

    public boolean isBlack(int color) {
        return ColorUtils.getGrayscale(color) <= threshold;
    }

}
