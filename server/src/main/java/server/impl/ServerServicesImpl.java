package server.impl;

import client.ClientProcessor;
import client.impl.ClientProcessorImpl;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.ServerServices;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;

public class ServerServicesImpl implements ServerServices {

    private static final Logger logger = LogManager.getLogger(ServerServicesImpl.class);

    private ServerSocket server = null;
    private boolean isRunning = true;

    private static final HashMap<Long, User> users = new HashMap<>();
    private static final HashMap<Long, ClientProcessor> clients = new HashMap<>();

    public ServerServicesImpl(String host, int port){
        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open() {
        logger.info("Server Initialized.");
        Thread t = new Thread(() -> {
            while(isRunning){
                try {
                    Socket client = server.accept();
                    logger.info("Client Connection received.");
                    Thread t1 = new Thread(new ClientProcessorImpl(client, this));
                    t1.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                server = null;
            }
        });

        t.start();
    }

    @Override
    public ClientProcessor findClient(long id) {
        synchronized (clients) {
            return clients.get(id);
        }
    }

    @Override
    public void addUser(User user, ClientProcessor clientProcessor, Collection<User> clientUsers) {
        synchronized (users) {
            clientUsers.addAll(users.values());

            synchronized (clients) {
                users.put(user.getId(), user);
                clients.put(user.getId(), clientProcessor);
            }
        }
    }

    @Override
    public void removeUser(long id) {
        synchronized (users) {
            synchronized (clients) {
                users.remove(id);
                clients.remove(id);
            }
        }
    }

    @Override
    public void close() {
        isRunning = false;
    }
}
