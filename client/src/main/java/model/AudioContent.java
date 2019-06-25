package model;

public class AudioContent extends ChunkContent implements Comparable<AudioContent> {

    private static final long serialVersionUID = 8663463112800513245L;
    public static final transient int MAX_BYTE_SIZE = 1024;

    private long audioId;

    public static AudioContent newInstance(long fileId, long chunkNumber, byte[] data) {
        return new AudioContent(fileId, chunkNumber, data);
    }

    private AudioContent(long audioId, long chunkNumber, byte[] data) {
        super(chunkNumber, data);
        this.audioId = audioId;

    }

    public long getAudioId() {
        return audioId;
    }

    public void setAudioId(long audioId) {
        this.audioId = audioId;
    }

    @Override
    public int compareTo(AudioContent o) {
        int sameFile = Long.compare(audioId, o.audioId);
        if (sameFile != 0) return sameFile;
        return Long.compare(getChunkNumber(), o.getChunkNumber());
    }

    @Override
    public String toString() {
        return "AudioContent{" +
                "audioId=" + audioId +
                ", chunkNumber=" + getChunkNumber() +
                '}';
    }
}
