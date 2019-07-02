package model;

public class ScreenInformation implements Content {

    private static final long serialVersionUID = 7670023109434647163L;

    private int width;
    private int height;

    public static ScreenInformation newInstance(int width, int height) {
        return new ScreenInformation(width, height);
    }

    private ScreenInformation(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "ScreenInformation{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
