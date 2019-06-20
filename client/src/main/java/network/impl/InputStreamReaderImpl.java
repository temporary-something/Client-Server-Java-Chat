package network.impl;


import model.Response;
import network.InputStreamListener;
import network.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

public class InputStreamReaderImpl implements Runnable, InputStreamReader {

    private static final Logger logger = LogManager.getLogger(InputStreamReaderImpl.class);

    private ObjectInputStream is;
    private List<InputStreamListener> listeners;

    private boolean isRunning = true;

    @Override
    public void open(ObjectInputStream ois) {
        this.is = ois;
        listeners = new LinkedList<>();
    }

    @Override
    public boolean addListener(InputStreamListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(InputStreamListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public void close() {
        isRunning = false;
    }

    public void run() {
        try {
            while (isRunning) {
                final Response response = (Response)is.readObject();
                for (final InputStreamListener listener : listeners) {
                    Thread t = new Thread(() -> listener.handleResponses(response));
                    t.start();
                }
            }
        } catch (SocketException | EOFException e) {
            logger.info("End of connection ...");
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e);
        }
        logger.info("Leaving InputStreamReaderImpl ...");
    }
}
