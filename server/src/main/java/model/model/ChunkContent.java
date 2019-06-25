package model.model;

import model.Content;

public abstract class ChunkContent implements Content {

    private static final long serialVersionUID = -2706195291822704465L;

    private long chunkNumber;
    private byte[] data;

    ChunkContent(long chunkNumber, byte[] data) {
        this.chunkNumber = chunkNumber;
        this.data = data;
    }

    public long getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(long chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
