package model;

public class AudioMessageContent extends MessageContent {

    private static final long serialVersionUID = 130355463408756959L;

    private long audioId;

    public static AudioMessageContent newInstance(AudioDescriptor audioDescriptor) {
        return new AudioMessageContent("Vocal Message", audioDescriptor.getAudioId());
    }

    private AudioMessageContent(String message, long audioId) {
        super(message);
        this.audioId = audioId;
    }

    public long getAudioId() {
        return audioId;
    }

    public void setAudioId(long audioId) {
        this.audioId = audioId;
    }

    @Override
    public String toString() {
        return "AudioMessageContent{" +
                "audioId=" + audioId +
                '}';
    }
}
