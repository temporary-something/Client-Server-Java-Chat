package controller.impl;

import com.google.inject.Inject;
import controller.ScreenFunctionalities;
import model.Frame;
import view.ScreenView;

public class ScreenController implements ScreenFunctionalities {

    @Inject private ScreenView screenView;

    @Override
    public void updateScreen(Frame frame) {
        screenView.updateScreen(frame);
    }
}
