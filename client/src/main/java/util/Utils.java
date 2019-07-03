package util;

import model.Event;
import model.MouseEvent;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.RobotPeer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Utils {

    private static final Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    /**
     * Original code from :
     * @author Dominic
     * @since 16-Oct-16
     * Website: www.dominicheal.com
     * Github: www.github.com/DomHeal
     *
     * Defines an audio format.
     */
    public static AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    /**
     * Resizes an image to an absolute width and height (the image may not be proportional)
     *
     * Inspired from : https://www.codejava.net/java-se/graphics/how-to-resize-images-in-java
     */
    public static BufferedImage resize(BufferedImage inputImage, int scaledWidth, int scaledHeight){
        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return outputImage;
    }

    public static byte[] toByteArray(BufferedImage image, String type) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            ImageIO.write(image, type, out);

            byte[] bts =  out.toByteArray();
            System.out.println(bts.length);
            return bts;
        }
    }

    public static int getScreenWidth() {
        return screen.width;
    }

    public static int getScreenHeight() {
        return screen.height;
    }
}
