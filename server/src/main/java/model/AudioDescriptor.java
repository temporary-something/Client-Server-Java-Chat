package model;

public class AudioDescriptor extends AudioBasicInformation {

    private static final long serialVersionUID = -8297717247921314814L;

    private long chunksTotalNumber;

    public static AudioDescriptor newInstance(long chunksTotalNumber) {
        return new AudioDescriptor(chunksTotalNumber);
    }

    private AudioDescriptor(long chunksTotalNumber) {
        super();
        this.chunksTotalNumber = chunksTotalNumber;
    }

    public long getChunksTotalNumber() {
        return chunksTotalNumber;
    }

    public void setChunksTotalNumber(long chunksTotalNumber) {
        this.chunksTotalNumber = chunksTotalNumber;
    }

    @Override
    public String toString() {
        return "AudioDescriptor{" +
                "audioId=" + getAudioId() +
                ", chunksTotalNumber=" + chunksTotalNumber +
                '}';
    }
}
