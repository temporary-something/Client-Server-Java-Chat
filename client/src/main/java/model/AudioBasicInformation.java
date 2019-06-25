package model;

public class AudioBasicInformation implements Content {

    private static final long serialVersionUID = -1409050112553260320L;

    private static long count = 0;
    private long audioId;

    public static AudioBasicInformation newInstance(final long audioId) {
        return new AudioBasicInformation(audioId);
    }

    private AudioBasicInformation(long audioId) {
        this.audioId = audioId;
    }

    AudioBasicInformation() {
        super();
        this.audioId = count++;
    }

    public long getAudioId() {
        return audioId;
    }

    public void setAudioId(long audioId) {
        this.audioId = audioId;
    }

    @Override
    public String toString() {
        return "AudioBasicInformation{" +
                "audioId=" + audioId +
                '}';
    }
}
