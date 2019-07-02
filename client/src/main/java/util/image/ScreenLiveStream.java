package util.image;

import model.ScreenInformation;
import model.User;

import java.awt.*;
import java.io.IOException;

public interface ScreenLiveStream {

    void startStreaming(User destination, ScreenInformation screenInformation) throws IOException, AWTException;
    void stopStreaming();
}
