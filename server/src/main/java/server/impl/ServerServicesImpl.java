package server.impl;

import client.ClientProcessor;
import client.impl.ClientProcessorImpl;
import model.User;
import server.ServerServices;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class ServerServicesImpl implements ServerServices {

    private ServerSocket server = null;
    private boolean isRunning = true;

    private static final HashMap<Long, User> users = new HashMap<>();
    private static final HashMap<Long, ClientProcessor> clients = new HashMap<>();

    @Override
    public void open() {
        Thread t = new Thread(() -> {
            while(isRunning){
                try {
                    Socket client = server.accept();
                    System.out.println("Client Connection received.");
                    Thread t1 = new Thread(new ClientProcessorImpl(client));
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
    public Collection<User> addUser(User user, ClientProcessor clientProcessor) {
        final Collection<User> usersBefore = new LinkedList<>();
        synchronized (users) {
            for (User u : users.values()) {
                usersBefore.add(u.clone());
            }
            synchronized (clients) {
                users.put(user.getId(), user);
                clients.put(user.getId(), clientProcessor);
            }
        }
        return usersBefore;
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
