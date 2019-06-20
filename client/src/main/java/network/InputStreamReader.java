package network;

import java.io.ObjectInputStream;

public interface InputStreamReader {

    void open(ObjectInputStream ois);
    void close();
    boolean addListener(InputStreamListener listener);
    boolean removeListener(InputStreamListener listener);
}
