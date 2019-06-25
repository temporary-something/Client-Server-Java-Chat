package util.voice;

import model.User;

public interface VoiceRecorder {

    void captureAudio(User destination);
    void endRecording();
}
