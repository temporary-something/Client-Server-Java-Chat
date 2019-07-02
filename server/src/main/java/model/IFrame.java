package model;

public class IFrame implements Frame {

    private static final long serialVersionUID = -3327912380543047507L;

    private byte[] image;

    public static IFrame newInstance(byte[] image) {
        return new IFrame(image);
    }

    private IFrame(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }
}
