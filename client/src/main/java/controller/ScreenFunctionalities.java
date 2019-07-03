package controller;

import model.Frame;
import model.MouseEvent;
import model.User;

public interface ScreenFunctionalities {

    void updateScreen(User source, Frame frame);
    void sendEvent(MouseEvent event);
}
