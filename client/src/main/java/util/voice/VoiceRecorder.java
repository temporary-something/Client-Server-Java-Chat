package util.voice;

import model.User;

public interface VoiceRecorder {

    void captureAudio();
    void endRecording(User destination);
}
